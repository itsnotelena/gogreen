package server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.EmailException;
import server.exceptions.UserExistsException;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.SolarState;
import shared.models.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class UserController {

    private static final AtomicLong counter = new AtomicLong();

    private final UserRepository repository;

    private final LogRepository logRepository;

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

    /**
     * Creates and returns a user with the given username and password.
     *
     * @return The created user.
     */
    @PostMapping(value = UserEndpoints.SIGNUP)
    public User createUser(@RequestBody User user) throws UserExistsException, EmailException {
        user.setPassword(hashPassword(user.getPassword())); // Hash the password


        // Catch duplicate exception
        try {
            repository.save(user); // Save the user to the database
        } catch (DataIntegrityViolationException e) {
            throw new UserExistsException();
        }

        if (!user.validateEmail()) {
            throw new EmailException();
        }



        user.setPassword(""); // Don't leak the (even the hashed) password
        try {
            System.out.println(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * The method returns how many points a user has according to the logs.
     *
     * @param authentication Takes a user by which the log repository is sorted.
     * @return User points.
     */
    @GetMapping(value = UserEndpoints.ACTIONLIST)
    public int actionList(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        user.setPassword("");
        int points = calcPoints(user);
        return points + getStateSolar(authentication).getPoints();
    }

    /**
     * Calculates leader board points.
     * @param username Takes a username of leader board.
     * @return Points of that user.
     */
    @PostMapping(value = UserEndpoints.GETOTHERUSERPOINTS)
    public int getOtherPoints(@RequestBody String username) {
        User user = repository.findUserByUsername(username);
        int points = calcPoints(user);
        return points;
    }

    /**
     * Method to be used to calculate points by username.
     * @param user For calculating user's points.
     * @return Points.
     */
    public int calcPoints(User user) {
        int points = 0;
        List<Log> list = logRepository.findByUser(user);
        if (list == null) {
            return 0;
        }
        for (Log log : list) {
            if (!log.getAction().equals(Action.SOLAR)) {
                points = points + log.getAction().getPoints();
            }
        }
        return points;
    }


    /**
     * The method returns a list of logs of a user to be displayed on the main screen.
     *
     * @param authentication To identify user.
     * @return The list of user logs.
     */
    @GetMapping(value = UserEndpoints.LOGS)
    public List<Log> getLogs(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        return logRepository.findByUser(user);
    }

    /**
     * Returns the state of the solar panels.
     * @param authentication Authentication details of the useer
     * @return An array representing a pair of the state of the button
     *          and the amount of points gathered by the solar panels.
     */
    @GetMapping(value = "/solar")
    public SolarState getStateSolar(Authentication authentication) {
        int points = 0;
        int total = 0;
        Log lastLog = null;
        for (Log log : getLogs(authentication)) {
            if (log.getAction().equals(Action.SOLAR)) {
                total++;
                if (total % 2 == 1) {
                    lastLog = log;
                } else {
                    LocalDate dateLatest = LocalDate.ofInstant(log.getDate().toInstant(),
                            ZoneId.systemDefault());
                    LocalDate datePrevious = LocalDate.ofInstant(lastLog.getDate().toInstant(),
                            ZoneId.systemDefault());
                    points += Action.SOLAR.getPoints()
                            * Period.between(datePrevious, dateLatest).getDays();
                }
            }
        }

        if (total % 2 == 1) {
            LocalDate datePrevious = LocalDate.ofInstant(lastLog.getDate().toInstant(),
                    ZoneId.systemDefault());
            points += Action.SOLAR.getPoints()
                    * Period.between(datePrevious, LocalDate.now()).getDays();
        }
        return new SolarState(points, total % 2 == 1);
    }

    /**
     * Returns a list of all users.
     * @return Lists of users.
     */
    @GetMapping(value = UserEndpoints.LEADERBOARD)
    public List<User> getLeaderBoard() {
        List<User> user = repository.findAll();
        return user;
    }

    /**
     * Searches for users.
     * @param username Takes a string to be searched in the user repo.
     * @return List with matching usernames.
     */
    @PostMapping(value = UserEndpoints.SEARCH)
    public List<User> search(@RequestBody String username) {
        List<User> users = repository.findAll();
        List<User> usersToReturn = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername().startsWith(username)) {
                usersToReturn.add(user);
            }
        }
        if (usersToReturn.isEmpty()) {
            return new ArrayList<>();
        }
        return usersToReturn;
    }

    /**
     * Adds the provided user to the current user's following set.
     * @param username Username of the User to add.
     * @param authentication Of the current user.
     * @return The added User.
     */
    @PostMapping(value = UserEndpoints.FOLLOW)
    public User addFollow(@RequestBody String username, Authentication authentication) {
        User current = repository.findUserByUsername(authentication.getName());
        User user = repository.findUserByUsername(username);
        if (!current.getUsername().equals(user.getUsername())) {

            current.getFollowing().add(user);
            repository.save(current);
        }
        return user;
    }

    /**
     * Removes user from 'following' set.
     * @param user To be removed.
     * @param authentication The user who is unfollowing.
     * @return The unfollowed user.
     */
    @PostMapping(value = UserEndpoints.UNFOLLOW)
    public User unfollow(@RequestBody User user, Authentication authentication) {
        User current = repository.findUserByUsername(authentication.getName());
        current.getFollowing().remove(user);
        repository.save(current);
        System.out.println(current.getFollowing().toArray().toString());
        return user;
    }

    /**
     * Returns a set of the 'following'.
     * @param authentication The user whose set is returned.
     * @return The set of the followed users.
     */
    @GetMapping(value = UserEndpoints.FOLLOWLIST)
    public Set<User> viewFollowList(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        Set<User> friends = user.getFollowing();
        return friends;
    }

}
