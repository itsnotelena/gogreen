package client.services;

import client.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class TestUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private String url = AppConfig.getRootUri();

    private User testUser;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");
        testUser.setEmail("test@test.com");
    }

    @Test
    public void testSignUp() throws Exception{
        User response = new User();
        response.setPassword("");
        response.setUsername("test");
        response.setEmail("test@test.com");

        mockServer.expect(ExpectedCount.once(), requestTo(url + UserEndpoints.SIGNUP))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(response)));

        boolean toTest = userService.createAccount(testUser);

        mockServer.verify();

        Assert.assertTrue(toTest);
    }

    @Test
    public void testLoginOK() throws Exception{
        String request = new ObjectMapper().writeValueAsString(testUser);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set(HttpHeaders.AUTHORIZATION, "Bearer: test");
        mockServer.expect(ExpectedCount.once(), requestTo(url + UserEndpoints.LOGIN))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(request))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(responseHeader));

        boolean toTest = userService.login(testUser);

        mockServer.verify();

        Assert.assertTrue(toTest);
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        User wrongUser = new User();
        wrongUser.setPassword("wrong");
        wrongUser.setUsername("test");
        wrongUser.setEmail("wrong@wrong.com");
        mockServer.expect(ExpectedCount.once(), requestTo(url + UserEndpoints.LOGIN))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        boolean toTest = userService.login(wrongUser);

        mockServer.verify();
        Assert.assertFalse(toTest);
    }






}
