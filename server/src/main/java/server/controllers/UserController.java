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
import shared.models.Points;
import shared.models.User;
import shared.models.Log;

import java.util.ArrayList;
import java.util.Iterator;
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

    @GetMapping(value = UserEndpoints.ACTIONLIST)
    public int actionList(Authentication authentication) {
        ArrayList<Action> list = new ArrayList<>();
        User user = repository.findUserByUsername(authentication.getName());
        user.setPassword("");
        int points = 0;
        long key = user.getId();
        System.out.println(key);

        Iterable<Log> findAll = logRepository.findAll();
        Iterator<Log> iter = findAll.iterator();

        while (iter.hasNext()) {
            Log log = iter.next();
            if (log.getUser().getId() == key) {
                list.add(log.getAction());
            }
        }

        if (list == null){
            return 0;
        }

        for (int i = 0; i < list.size(); i++) {
            points = calcPoints(list.get(i));
        }
        return points;
    }

    @GetMapping(value = UserEndpoints.LOGS)
    public List<Log> getLogs(Authentication authentication)
    {
        User user = repository.findUserByUsername(authentication.getName());
        return logRepository.findByUser(user);
    }

    public int calcPoints(Action action){
        int points = 0;
        if (action.equals(Action.VEGETARIAN)){
            points =+ Points.VEGETARIAN;
        }
        if (action.equals(Action.TEMP)){
            points =+ Points.TEMP;
        }
        if (action.equals(Action.BIKE)){
            points =+ Points.BIKE;
        }
        if (action.equals(Action.LOCAL)){
            points =+ Points.LOCAL;
        }
        if (action.equals(Action.PUBLIC)){
            points =+ Points.PUBLIC;
        }
        return points;
    }

}
