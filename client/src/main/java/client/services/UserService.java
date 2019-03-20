package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.User;

import java.util.Date;
import java.util.List;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method that makes a post request to the database to register the new user.
     * @param user The user to be registered
     * @return true if the user was successfully registered; false otherwise
     */
    public boolean createAccount(User user) {
        User response;
        try {
            response = restTemplate.postForObject(UserEndpoints.SIGNUP, user, User.class);
        } catch (HttpClientErrorException e) {
            return false;
        }
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
        ResponseEntity<User> response;
        try {
            response = restTemplate.exchange(UserEndpoints.LOGIN, HttpMethod.POST, req, User.class);
        } catch (HttpClientErrorException e) {
            return false;
        }

        if (response.getHeaders().get("Authorization") == null) {
            return false;
        }

        List<String> auth = response.getHeaders().get("Authorization");
        String token = auth.get(0);
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", token);
            return execution.execute(request, body);
        });

        return response.getStatusCodeValue() / 100 == 2;
        // TODO: Login to the backend
        // Make sure to globally set the authentication header for further communications
    }

    /**
     * Methods logs to the database that the user has eaten a vegetarian meal.
     */
    public long ateVegMeal() {
        Log req = new Log();
        req.setAction(Action.VegetarianMeal);
        req.setDate(new Date());
        restTemplate.postForObject("/log", req, Log.class);
        Long newPoints = restTemplate.postForObject("/action", Action.VegetarianMeal, Long.class);
        System.out.println("Successfully added a log to the table");
        return newPoints;
    }

    public long getPoints() {
        Long response = restTemplate.getForObject("/points", Long.class);
        return response != null ? response : 0L;
    }
}


