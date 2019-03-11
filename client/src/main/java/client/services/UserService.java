package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.User;

import java.util.Objects;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

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
     * @param user takes the User as input which should be authorized.
     * @return returns true if the user is authorized, otherwise false.
     */
    public boolean login(User user) {
        HttpEntity<User> req = new HttpEntity<>(user);
        ResponseEntity<User> response =
                restTemplate.exchange(UserEndpoints.LOGIN, HttpMethod.POST, req, User.class);

        String token = Objects.requireNonNull(
                response.getHeaders().get("Authorization")).get(0);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", token);
            return execution.execute(request, body);
        });

        boolean code = response.getStatusCodeValue() / 100 == 2;

        return code;
        // TODO: Login to the backend
        // Make sure to globally set the authentication header for further communications
    }
}


