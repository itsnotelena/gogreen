package client.services;

        import client.AppConfig;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
        import org.junit.AfterClass;
        import org.junit.Assert;
        import org.junit.Before;
        import org.junit.Test;
        import org.junit.runner.RunWith;
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

        String asdf = new ObjectMapper().writeValueAsString(list);

        mockServer.expect(requestTo(url + UserEndpoints.SEARCH))
                .andExpect(method(HttpMethod.POST))
//                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(username))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(list)));

        List<User> response = userService.search(username);

        mockServer.verify();


        Assert.assertEquals(list, response);


    }


}