package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.models.Gender;

@Service("UserService")
public class UserService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createAccount(String username, String password, String email, Gender gender) {
        // Create Account
    }

    public void login(String username, String password) {
        // Login
    }
}


