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
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository repository;

    private final LogRepository logRepository;

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Creates and returns a user with the given username and password.
     *
     * @return the created user
     */
    @PostMapping(value = UserEndpoints.SIGNUP)
    public User createUser(@RequestBody User user) throws UserExistsException {
        user.setPassword(hashPassword(user.getPassword())); // Hash the password

        // Catch duplicate exception
        try {
            repository.save(user); // Save the user to the database
        } catch (DataIntegrityViolationException e) {
            throw new UserExistsException();
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
     * @param authentication takes a user by which the log repository is sorted.
     * @return user points.
     */
    @GetMapping(value = UserEndpoints.ACTIONLIST)
    public int actionList(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        user.setPassword("");
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
        return points + getStateSolar(authentication).getPoints();
    }

    /**
     * The method returns a list of logs of a user to be displayed on the main screen.
     *
     * @param authentication to identify user.
     * @return the list of user logs.
     */
    @GetMapping(value = UserEndpoints.LOGS)
    public List<Log> getLogs(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        return logRepository.findByUser(user);
    }

    /**
     * Returns the state of the solar panels.
     *
     * @param authentication authentication details pof the user
     * @return an array representing a pair of the state of the button
     * and the amount of points gathered by the solar panels.
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
                    LocalDate dateLatest = log.getDate();
                    LocalDate datePrevious = lastLog.getDate();
                    points += Action.SOLAR.getPoints()
                            * Period.between(datePrevious, dateLatest).getDays();
                }
            }
        }
        if (total % 2 == 1) {
            LocalDate datePrevious = lastLog.getDate();
            points += Action.SOLAR.getPoints()
                    * Period.between(datePrevious, LocalDate.now()).getDays();
        }
        return new SolarState(points, total % 2 == 1);
    }

    @GetMapping(value = UserEndpoints.LEADERBOARD)
    public List<User> getLeaderBoard() {
        return repository.findByOrderByFoodPointsDesc();
    }

    @PostMapping(value = UserEndpoints.SEARCH)
    public User search(@RequestBody String username) {
        return repository.findUserByUsername(username);
    }

    @PostMapping(value = UserEndpoints.FOLLOW)
    public User addFollow(@RequestBody User user, Authentication authentication) {
        User current = repository.findUserByUsername(authentication.getName());
        if (!current.getUsername().equals(user.getUsername())) {

            current.getFollowing().add(user);
            repository.save(current);
        }
        return current;
    }

    @GetMapping(value = UserEndpoints.FOLLOWLIST)
    public Set<User> viewFollowList(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        Set<User> friends = user.getFollowing();
        return friends;
    }

    @GetMapping(value = UserEndpoints.TODAYPROGRESS)
    public int getPointsToday(Authentication authentication) {
        int points = 0;
        for (Log log : getLogs(authentication)) {
            if (Period.between(log.getDate(), LocalDate.now()).getDays() == 0
                    && !log.getAction().equals(Action.SOLAR)) {
                points += log.getAction().getPoints();
            }
        }
        return points;
    }

}
