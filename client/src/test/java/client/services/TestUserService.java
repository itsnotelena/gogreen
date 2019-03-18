package client.services;

import client.AppConfig;
import client.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.User;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class TestUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private String url = AppConfig.getRootUri();

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testSignUp() {
        User testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");

        mockServer.expect(requestTo(url + UserEndpoints.SIGNUP))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());

        userService.createAccount(testUser);

        mockServer.verify();
    }

    @Test
    public void testLogin() {
        User testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");

        mockServer.expect(requestTo(url + UserEndpoints.LOGIN))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());

        userService.login(testUser);

        mockServer.verify();
    }

    @Test
    public void testVegMeal() {
        // TODO: Implement
    }


    @Test
    public void testGetPoints() {
        User testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");

        mockServer.expect(requestTo(url + "/points"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withNoContent());

        userService.getPoints();

        mockServer.verify();
    }
}
