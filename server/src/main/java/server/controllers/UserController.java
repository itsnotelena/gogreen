package server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.exceptions.UserExistsException;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.User;
import shared.models.Log;

import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

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

    //TODO: Give different points based on the action

    /**
     * Updates the user points based on the action and returns the new amount of points.
     *
     * @param action         The action for which the user gets the points
     * @param authentication Details to identify the user
     * @return The new amount of points
     */
    @PostMapping(value = "/action")
    public long updatePoints(@RequestBody Action action,
                             Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        if (action.equals(Action.VegetarianMeal)) {
            user.setFoodPoints(user.getFoodPoints() + 100);
            repository.save(user);
        }
        return user.getFoodPoints();
    }

    @GetMapping(value = "/points")
    public long getPoints(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        return user.getFoodPoints();
    }

    @GetMapping(value="/logs")
    public List<Log> getLogs(Authentication authentication)
    {
        User user = repository.findUserByUsername(authentication.getName());
        return logRepository.findByUser(user);
    }

    @GetMapping(value = "/leaderboard")
    public List<User> getLeaderBoard(){
        return repository.findByOrderByFoodPointsDesc();
    }

    @RequestMapping(value = "/search")
    public User search(@RequestBody String username){
        return repository.findUserByUsername(username);
    }
}
