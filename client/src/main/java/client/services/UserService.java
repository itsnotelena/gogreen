package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

    private User user;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User getUser() {
        return user;
    }

    public boolean createAccount(User user) {
        User response = restTemplate.postForObject(UserEndpoints.SIGNUP, user, User.class);
        return response != null;
    }

    /**
     * The method authorizes a user from the database and logs in.
     *
     * @param user takes the User as input which should be authorized.
     * @return returns true if the user is authorized, otherwise false.
     */
    public boolean login(User user) {
        HttpEntity<User> req = new HttpEntity<>(user);
        ResponseEntity<User> response =
                restTemplate.exchange(UserEndpoints.LOGIN, HttpMethod.POST, req, User.class);
        List<String> auth = Objects.requireNonNull(
                response.getHeaders().get("Authorization"));
        String token = auth.get(0);
        this.user = user;
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", token);
            return execution.execute(request, body);
        });

        return response.getStatusCodeValue() / 100 == 2;
        // TODO: Login to the backend
        // Make sure to globally set the authentication header for further communications
    }

    /**
     * Methods logs to the database that the user has eaten a vegetarian meal
     */
    public void ateVegMeal() {
        Log req = new Log();
        //TODO: set respective user so that we can keep track of points.
        req.setUser(null);
        req.setAction(Action.VegetarianMeal);
        req.setDate(new Date());
        Log response = restTemplate.postForObject("/log", req, Log.class);
        System.out.println("Succesfully added a log to the table");
    }
}


