package server;

import models.HelloWorld;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HelloWorldController {

    private static final AtomicLong counter = new AtomicLong();

    private final UserRepository repository;

    @Autowired
    public HelloWorldController(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and returns a user with the given username and password.
     * @return the created user
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createAndReturnUser(@RequestBody User user) {
        // User user2 = new User("user", "pass");
        repository.save(user);
        return user;
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HelloWorld hello() {
        return new HelloWorld(counter.getAndIncrement(), "Hello World!");
    }
}
