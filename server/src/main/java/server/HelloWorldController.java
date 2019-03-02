package server;

import lombok.AllArgsConstructor;
import models.HelloWorld;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class HelloWorldController {

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
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createAndReturnUser(@RequestBody User user) {
        user.setPassword(hashPassword(user.getPassword())); // Hash the password
        repository.save(user); // Save the user to the database
        user.setPassword(""); // Don't leak the (even the hashed) password
        return user;
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HelloWorld hello() {
        return new HelloWorld(counter.getAndIncrement(), "Hello World!");
    }
}
