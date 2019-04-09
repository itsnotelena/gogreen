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
import shared.models.SolarState;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");
        testUser.setEmail("test@gmail.com");
        objectMapper.registerModule(new JavaTimeModule());
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

    @Test
    public void testVegMeal() throws Exception {
        Log req = new Log();
        req.setAction(Action.VEGETARIAN);
        req.setDate(LocalDate.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String response = mapper.writeValueAsString(req);
        mockServer.expect(ExpectedCount.once(), requestTo(url + UserEndpoints.LOGS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.action").value(Action.VEGETARIAN.toString()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        userService.madeAction(Action.VEGETARIAN,1);

        mockServer.verify();
    }


    @Test
    public void testGetPoints() {
        mockServer.expect(requestTo(url + UserEndpoints.ACTIONLIST))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Integer.toString(0)));

        long response = userService.getPoints();

        mockServer.verify();

        Assert.assertEquals(0L, response);
    }

    @Test
    public void testFollowUser()throws Exception{

        User followUser = new User();
        followUser.setPassword("follow");
        followUser.setUsername("follow");
        followUser.setEmail("follow@gmail.com");
        String responseT = new ObjectMapper().writeValueAsString(followUser);
        mockServer.expect(requestTo(url + UserEndpoints.FOLLOW))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseT));
        User response = userService.addFollow(followUser);
        mockServer.verify();

        Assert.assertEquals(responseT, new ObjectMapper().writeValueAsString(response));
    }

    @Test
    public void actionTempAmountTest() throws Exception {
        Log log = new Log();
        log.setAction(Action.TEMP);
        log.setUser(testUser);
        log.setAmount(4);
        log.setDate(LocalDate.now());
        mockServer.expect(requestTo(url + UserEndpoints.LOGS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.action").value(Action.TEMP.toString()))
                .andExpect(jsonPath("$.amount").value(4))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(log)));

        userService.madeAction(Action.TEMP, 4);

        mockServer.verify();
    }

    @Test
    public void getFollwingPointsTest() throws Exception {
        String request = "username";
        mockServer.expect(requestTo(url + UserEndpoints.GETOTHERUSERPOINTS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(request))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(0)));

        String username = "username";
        int points = userService.getFollowingPoints(username);

        mockServer.verify();
        Assert.assertEquals(0, points);
    }

    @Test
    public void getPointsTodayTest() throws Exception {
        mockServer.expect(requestTo(url + UserEndpoints.TODAYPROGRESS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(0)));

        userService.getPointsToday();

        mockServer.verify();
    }

    @Test
    public void getSolarStateTest() throws Exception {
        SolarState response = new SolarState();
        response.setEnabled(true);
        response.setPoints(Action.SOLAR.getPoints());
        mockServer.expect(requestTo(url + UserEndpoints.SOLAR))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response)));

        SolarState toTest = userService.getStateSolar();

        mockServer.verify();
        Assert.assertEquals(response, toTest);
    }

    @Test
    public void createAccountExceptionTest() throws Exception {
        User user = new User();
        user.setUsername("alreadyInUse");
        mockServer.expect(requestTo(url + UserEndpoints.SIGNUP))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        boolean toTest = userService.createAccount(user);

        mockServer.verify();

        Assert.assertFalse(toTest);
    }

    @Test
    public void nullResponseSignupTest() throws Exception {
        User user = new User();
        user.setUsername("alreadyInUse");
        mockServer.expect(requestTo(url + UserEndpoints.SIGNUP))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
                .andRespond(withStatus(HttpStatus.OK).body(""));

        boolean toTest = userService.createAccount(user);

        mockServer.verify();

        Assert.assertFalse(toTest);
    }

    @Test
    public void failedSearchTest() throws Exception {
        String username = "test";
        ArrayList<User> response = new ArrayList<>();
        mockServer.expect(requestTo(url + UserEndpoints.SEARCH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(username))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response)));

        List<User> toTest = userService.search(username);

        mockServer.verify();

        Assert.assertTrue(toTest.isEmpty());
    }

    @Test
    public void unsuccessfulLoginTest() throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("wrongPassword");
        mockServer.expect(requestTo(url + UserEndpoints.LOGIN))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(user)));

        boolean toTest = userService.login(user);

        mockServer.verify();

        Assert.assertFalse(toTest);
    }

    @Test
    public void serverErrorLoginTest() throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("wrongPassword");
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set(HttpHeaders.AUTHORIZATION, "Bearer: test");
        mockServer.expect(requestTo(url + UserEndpoints.LOGIN))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(user)))
                .andRespond(withStatus(HttpStatus.valueOf(300)).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(user)).headers(responseHeader));

        boolean toTest = userService.login(user);

        mockServer.verify();

        Assert.assertFalse(toTest);
    }

}
