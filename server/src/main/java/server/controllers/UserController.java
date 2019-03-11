package server.controllers;

import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.UserExistsException;
import server.repositories.UserRepository;
import server.security.JWTAuthenticationFilter;
import shared.endpoints.UserEndpoints;
import shared.models.User;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class UserController {

    private static final AtomicLong counter = new AtomicLong();

    private final UserRepository repository;

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

    /**
     * Creates and returns a user with the given username and password.
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
        return user;
    }
}
