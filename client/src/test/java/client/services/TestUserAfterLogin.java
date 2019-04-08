package client.services;

        import client.AppConfig;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
        import org.junit.AfterClass;
        import org.junit.Assert;
        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.mindrot.jbcrypt.BCrypt;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpMethod;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
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
        import java.util.*;

        import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
        import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})

public class TestUserAfterLogin {
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
        User testUser = new User();
        testUser.setPassword("test");
        testUser.setUsername("test");
        testUser.setUsername("test@test.com");

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
        followUser.setEmail("follow@follow.com");
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
    public void testSearchUser()throws Exception{

        User searchUser = new User();
        searchUser.setPassword("search");
        searchUser.setUsername("search");
        searchUser.setEmail("search@search.com");
        String username = "search";
        List<User> list = new ArrayList<>();
        list.add(searchUser);

        mockServer.expect(requestTo(url + UserEndpoints.SEARCH))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(username))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(list)));

        List<User> response = userService.search(username);

        mockServer.verify();

        Assert.assertEquals(list, response);
    }

    @Test
    public void testGetUserDetails() throws Exception{

        String responseT = new ObjectMapper().writeValueAsString(testUser);

        mockServer.expect(requestTo(url + UserEndpoints.USER_INFO))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseT));

        User response = userService.getUser();

        mockServer.verify();

        Assert.assertEquals(responseT, new ObjectMapper().writeValueAsString(response));
    }

    @Test
    public void testSetPassword() throws Exception{
        String resp = new ObjectMapper().writeValueAsString(testUser);
        String password = "pass";

        mockServer.expect(requestTo(url + UserEndpoints.CHANGE_PASS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(password))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resp));

        userService.setPassword(password);

        mockServer.verify();
    }

    @Test
    public void testGetPointsToday() throws Exception{

        mockServer.expect(requestTo(url + UserEndpoints.TODAYPROGRESS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Integer.toString(0)));

        int points = userService.getPointsToday();

        mockServer.verify();

        Assert.assertTrue(points == 0);
    }

    @Test
    public void testGetSolarState() throws Exception{

        SolarState state = new SolarState();
        state.setEnabled(false);
        state.setPoints(0);

        String response = new ObjectMapper().writeValueAsString(state);

        mockServer.expect(requestTo(url + "/solar"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        SolarState test = userService.getStateSolar();

        mockServer.verify();

        Assert.assertTrue(test.getPoints() == 0);
        Assert.assertFalse(test.isEnabled());
    }

    @Test
    public void testFollowingPoints() throws Exception{

        mockServer.expect(requestTo(url + UserEndpoints.GETOTHERUSERPOINTS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(testUser.getUsername()))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Integer.toString(0)));

        int resp = userService.getFollowingPoints(testUser.getUsername());

        mockServer.verify();

        Assert.assertTrue(resp == 0);
    }

    @Test
    public void testRemoveFollow() throws Exception{

        User followUser = new User();
        followUser.setPassword("follow");
        followUser.setUsername("follow");
        followUser.setEmail("follow@follow.com");
        String responseT = new ObjectMapper().writeValueAsString(followUser);

        mockServer.expect(requestTo(url + UserEndpoints.UNFOLLOW))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseT));

        userService.removeFollow(followUser);

        mockServer.verify();
    }

    @Test
    public void testViewFollowList() throws Exception{

        User followUser = new User();
        followUser.setPassword("follow");
        followUser.setUsername("follow");
        followUser.setEmail("follow@follow.com");

        List<User> list = new ArrayList<>();
        list.add(followUser);

        mockServer.expect(requestTo(url + UserEndpoints.FOLLOWLIST))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(list)));

        List follow = userService.viewFollowList();

        mockServer.verify();

        Assert.assertTrue(follow.size()>0);
    }

    @Test
    public void testGetLeaderBoard() throws Exception{

        List<User> list = new ArrayList<>();
        list.add(testUser);

        mockServer.expect(requestTo(url + UserEndpoints.LEADERBOARD))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(list)));

        List lead = userService.getLeaderBoard();

        mockServer.verify();

        Assert.assertTrue(lead.size()>0);
    }

    @Test
    public void testGetLog() throws Exception{

        List<Log> list = new ArrayList<>();
        Log log = new Log();
        log.setAction(Action.PUBLIC);
        log.setUser(testUser);
        list.add(log);

        mockServer.expect(requestTo(url + UserEndpoints.LOGS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(list)));

        List resp = userService.getLog();

        mockServer.verify();


        Assert.assertTrue(resp.size()>0);
    }










}