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
import java.util.ArrayList;
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
     * Gets the points of a followed user.
     * @param username of the followed user.
     * @return that user's points.
     */
    public int getFollowingPoints(String username) {
        int response = restTemplate.postForObject(
                UserEndpoints.GETOTHERUSERPOINTS, username, int.class);
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
     * Searches for users.
     * @param username a string which will be checked.
     * @return a list of users whose usernames match the string.
     */
    public List<User> search(String username) {
        HttpEntity<String> request = new HttpEntity<>(username);
        ResponseEntity<List<User>> response =
                restTemplate.exchange(UserEndpoints.SEARCH, HttpMethod.POST, request,
                        new ParameterizedTypeReference<List<User>>() {
                        });
        if (response.getBody().isEmpty()) {
            return new ArrayList<User>();
        }
        return response.getBody();
    }

    /**
     * Adds user to the 'following' list.
     * @param user To be added.
     * @return The added user.
     */
    public User addFollow(User user) {
        User response = restTemplate.postForObject(
                UserEndpoints.FOLLOW, user.getUsername(), User.class);
        System.out.println(response);
        return response;
    }

    /**
     * Removes user from the 'following' list.
     * @param user to be removed.
     */
    public void removeFollow(User user) {
        User another = restTemplate.postForObject(UserEndpoints.UNFOLLOW, user, User.class);
        System.out.println("removed user: " + another.getUsername());
    }

    /**
     * Creates a view of the 'following' set.
     * @return that set.
     */
    public List<User> viewFollowList() {
        ResponseEntity<List<User>> response =
                restTemplate.exchange(
                        UserEndpoints.FOLLOWLIST,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<User>>() {
                        });

        List<User> followlist = response.getBody();
        return followlist;
    }

    public int getPointsToday() {
        return restTemplate.getForObject(UserEndpoints.TODAYPROGRESS, Integer.class);
    }

    /**
     * Returns the current user.
     * @return The user.
     */
    public User getUser() {
        return restTemplate.getForObject(UserEndpoints.USER_INFO, User.class);
    }

    /**
     * Sends a request to change the user's password.
     * @param password The new password value.
     */
    public void setPassword(String password) {
        restTemplate.postForObject(UserEndpoints.CHANGE_PASS, password, User.class);
    }
    public int getPointsToday() {
        return restTemplate.getForObject(UserEndpoints.TODAYPROGRESS, Integer.class);
    }

    /**
     * Returns the current user's username.
     * @return
     */
    public String getUsername() {
        return getUser().getUsername();
    }

    /**
     * Returns the current user's email.
     * @return
     */
    public String getEmail() { return getUser().getEmail(); }


}


