package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.SolarState;
import shared.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method that makes a post request to the database to register the new user.
     *
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
    public void madeAction(Action action, int amount) {
        Log req = new Log();
        req.setAction(action);
        req.setDate(LocalDate.now());
        if (action.equals(Action.TEMP)) {
            req.setAmount(amount);
        }
        restTemplate.postForObject(UserEndpoints.LOGS, req, Log.class);
        System.out.println("Successfully added a log to the table");
    }


    /**
     * Method that makes a request to the database and returns the total amount of points.
     *
     * @return the amounts of points a user has
     */
    public int getPoints() {
        int response = restTemplate.getForObject(UserEndpoints.ACTIONLIST, int.class);
        return response;
    }

    /**
     * Gets the state of the solar button (clicked or not)
     * and the amounts of points gathered by the solar panels.
     *
     * @return A pair composed of the state (clicked or not) and the points
     */
    public SolarState getStateSolar() {
        return restTemplate.getForObject("/solar", SolarState.class);
    }

    /**
     * This method requests a log list from the server.
     *
     * @return the log list.
     */
    public List<Log> getLog() {
        ResponseEntity<List<Log>> response = restTemplate.exchange(UserEndpoints.LOGS,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Log>>() {
                });
        List<Log> loglist = response.getBody();
        return loglist;
    }

    /**
     * Retrieves a list of users from the database sorted descending by score.
     * @return The list of users based on score
     */
    public List<User> getLeaderBoard() {
        ResponseEntity<List<User>> response =
                restTemplate.exchange(
                        UserEndpoints.LEADERBOARD,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {
                        });

        List<User> leaderlist = response.getBody();
        return leaderlist;
    }

    /**
     * Searches a user on the database by the username.
     * @param username The identifier by which to search
     * @return the searched user.
     */
    public User search(String username) {
        User response = restTemplate.postForObject(UserEndpoints.SEARCH, username, User.class);
        System.out.println(response);
        return response;
    }

    /**
     * Adds the user to this user's followed users.
     * @param user the user to follow.
     * @return the followed user
     */
    public User addFollow(User user) {
        User response = restTemplate.postForObject(UserEndpoints.FOLLOW, user, User.class);

        //response.getFollowing().forEach(e -> System.out.println(e));

        System.out.println(response);
        return response;
    }

    /**
     * Returns a set of your followed users.
     * @return Set of this user's followed people
     */
    public Set<User> viewFollowList() {
        ResponseEntity<Set<User>> response =
                restTemplate.exchange(
                        UserEndpoints.FOLLOWLIST,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Set<User>>() {
                        });

        Set<User> followlist = response.getBody();
        followlist.forEach(e -> System.out.println(e));
        return followlist;
    }

    public int getPointsToday() {
        return restTemplate.getForObject(UserEndpoints.TODAYPROGRESS, Integer.class);
    }


}


