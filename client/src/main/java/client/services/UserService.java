package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.User;

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

    public void login(String username, String password) {
        // TODO: Login to the backend
        // Make sure to globally set the authentication header for further communications
    }
}


