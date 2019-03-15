package client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

    private User user;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        ResponseEntity<User> response;
        try {
            response = restTemplate.exchange(UserEndpoints.LOGIN, HttpMethod.POST, req, User.class);
        } catch (HttpClientErrorException e) {
            return false;
        }
        List<String> auth = Objects.requireNonNull(
                response.getHeaders().get("Authorization"));
        String token = auth.get(0);
        this.user = user;
        user.setPassword("");
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
        User userKey = restTemplate.postForObject("/search/user",
                this.user.getUsername(), User.class);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(userKey));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Log req = new Log();
        req.setUser(userKey);
        req.setAction(Action.VegetarianMeal);
        req.setDate(new Date());
        Log response = restTemplate.postForObject("/log", req, Log.class);
        Long newPoints = restTemplate.postForObject("/action", Action.VegetarianMeal, Long.class);
        System.out.println("Succesfully added a log to the table");
        return newPoints;
    }
}


