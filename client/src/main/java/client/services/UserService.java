package client.services;

import javafx.util.Pair;
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
import shared.models.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
    public int madeAction(Action action) {
        Log req = new Log();
        req.setAction(action);
        req.setDate(new Date());
        restTemplate.postForObject(UserEndpoints.LOGS, req, Log.class);
        int  newPoints = restTemplate.getForObject(UserEndpoints.ACTIONLIST, int.class);
        System.out.println("Successfully added a log to the table");
        return newPoints;
    }

    public int getPoints() {
        int response = restTemplate.getForObject(UserEndpoints.ACTIONLIST, int.class);
        return response;
    }

    /**
     * Gets the state of the solar button (clicked or not)
     * and the amounts of points gathered by the solar panels.
     * @return A pair composed of the state (clicked or not) and the points
     */
    public Pair<Boolean, Integer> getStateSolar() {
        int points = 0;
        int total = 0;
        Log lastLog = null;
        for (Log log : getLog()) {
            if (log.getAction().equals(Action.SOLAR)) {
                if (total % 2 == 0) {
                    lastLog = log;
                } else {
                    LocalDate dateLatest = LocalDate.ofInstant(log.getDate().toInstant(),
                            ZoneId.systemDefault());
                    LocalDate datePrevious = LocalDate.ofInstant(lastLog.getDate().toInstant(),
                            ZoneId.systemDefault());
                    points += Action.SOLAR.getPoints()
                            * Period.between(datePrevious, dateLatest).getDays();
                }
                total++;
            }
        }
        return new Pair<>(total % 2 == 1, points);
    }

    /**
     * This method requests a log list from the server.
     *
     * @return the log list.
     */
    public List<Log> getLog() {
        ResponseEntity<List<Log>> response = restTemplate.exchange(UserEndpoints.LOGS,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Log>>(){});
        List<Log> loglist = response.getBody();
        return loglist;
    }

}


