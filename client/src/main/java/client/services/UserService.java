package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.User;

import java.io.IOException;
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

    public boolean login(User user) {
        // User response = restTemplate.postForObject(UserEndpoints.LOGIN, user, User.class);
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


