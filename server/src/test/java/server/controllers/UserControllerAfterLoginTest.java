package server.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.tomcat.jni.Local;
import org.apache.tomcat.util.http.parser.Authorization;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.SolarState;
import shared.models.User;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerAfterLoginTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    private MockMvc mvc;
    private User testUser = new User();

    private String UString;

    private User followUser = new User();

    private String FUString;

    private User followUser2 = new User();

    private String FU2String;

    private String authorization;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User currentUser;

    @Before
    public void setup() throws Exception {
        String username = "test";
        testUser.setUsername(username);
        testUser.setPassword("test");
        testUser.setEmail("test@test");
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword()
                + "\", \"email\": \"" + testUser.getEmail() + "\"}";

        // Remove the test user if it exists
        User toDelete = userRepository.findUserByUsername(username);
        if (toDelete != null) {
            userRepository.delete(toDelete);
        }

        this.mvc.perform(
                post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON).content(UString));
        authorization = this.mvc.perform(
                post(UserEndpoints.LOGIN).contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getHeader("Authorization");

        currentUser = userRepository.findUserByUsername("test");
    }


    @Test
    public void noLogUserTest() throws Exception {
        String response = this.mvc.perform(get(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Assert.assertTrue(mapper.readValue(response, List.class).isEmpty());
    }

    @Test
    public void getPointsNoActionTest() throws Exception {
        String result = this.mvc.perform(get(UserEndpoints.ACTIONLIST).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertEquals(0, Integer.parseInt(result));
    }

    @Test
    public void getPointsAfterActionTest() throws Exception {
        Log req = new Log();
        req.setAction(Action.VEGETARIAN);
        String postContent = new ObjectMapper().writeValueAsString(req);
        String toDelete = this.mvc.perform(post(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(postContent))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        String result = this.mvc.perform(get(UserEndpoints.ACTIONLIST).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertEquals(Action.VEGETARIAN.getPoints(), Integer.parseInt(result));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

    }

    @Test
    public void getSolarPoints_OnlyOneToggle_Test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        LocalDate datePrevious = LocalDate.now().minus(Period.ofDays(2)).atStartOfDay().toLocalDate();
        Log solar = new Log();
        solar.setDate(datePrevious);
        solar.setAction(Action.SOLAR);
        solar.setUser(testUser);
        String log = this.mvc.perform(post(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solar)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Log test = mapper.readValue(log, Log.class);
        test.setDate(datePrevious);
        logRepository.save(test);
        String response = this.mvc.perform(get("/solar").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        SolarState state = mapper.readValue(response, SolarState.class);
        Assert.assertEquals(2 * Action.SOLAR.getPoints(), state.getPoints());
        Assert.assertTrue(state.isEnabled());
    }

    @Test
    public void getSolarPoints_afterOnOff_Test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Log firstLog = new Log();
        Log secondLog = new Log();
        firstLog.setUser(testUser);
        secondLog.setUser(testUser);
        firstLog.setAction(Action.SOLAR);
        secondLog.setAction(Action.SOLAR);
        String toDelete1 = this.mvc.perform(post(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(firstLog)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        String toDelete2 = this.mvc.perform(post(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(secondLog)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        String response = this.mvc.perform(get("/solar").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        SolarState state = mapper.readValue(response, SolarState.class);
        Assert.assertEquals(0, state.getPoints());
        Assert.assertFalse(state.isEnabled());
    }

    @Test
    public void searchUserTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String Fusername = "follow";
        followUser.setUsername(Fusername);
        followUser.setPassword("test");
        followUser.setEmail("test@test");
        String request = mapper.writeValueAsString(followUser);
        this.mvc.perform(
                post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON).content(request));

        String search = this.mvc.perform(post(UserEndpoints.SEARCH).header(HttpHeaders.AUTHORIZATION, authorization)
                .content(followUser.getUsername()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        User[] result = mapper.readValue(search, User[].class);
        Assert.assertTrue(result.length >= 1);
    }

    @Test
    public void followUserTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String Fusername = "follow1";
        followUser.setUsername(Fusername);
        followUser.setPassword("test");
        FUString = "{\"username\": \"" + followUser.getUsername() + "\", \"password\": \"" + followUser.getPassword() + "\", \"email\": \"" + testUser.getEmail() + "\"}";

        this.mvc.perform(
                post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON).content(FUString));

        String follow = this.mvc.perform(post(UserEndpoints.FOLLOW).header(HttpHeaders.AUTHORIZATION, authorization)
                .content(followUser.getUsername()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        User result = mapper.readValue(follow, User.class);
        Assert.assertEquals(followUser.getUsername(), result.getUsername());

    }

    @Test
    public void getFollowListTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String Fusername = "follow2";
        followUser.setUsername(Fusername);
        followUser.setPassword("test");
        FUString = "{\"username\": \"" + followUser.getUsername() + "\", \"password\": \"" + followUser.getPassword() + "\", \"email\": \"" + testUser.getEmail() + "\"}";

        this.mvc.perform(
                post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON).content(FUString));

        String F2username = "follow3";
        followUser2.setUsername(F2username);
        followUser2.setPassword("test");
        FU2String = "{\"username\": \"" + followUser2.getUsername() + "\", \"password\": \"" + followUser2.getPassword() + "\", \"email\": \"" + testUser.getEmail() + "\"}";

        this.mvc.perform(
                post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON).content(FU2String));

        Set<User> follows = new HashSet<>();
        follows.add(followUser);
        follows.add(followUser2);

        this.mvc.perform(
                post(UserEndpoints.FOLLOW).contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, authorization).content(followUser.getUsername()));
        this.mvc.perform(
                post(UserEndpoints.FOLLOW).contentType(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, authorization).content(followUser2.getUsername()));

        String followlist = this.mvc.perform(get(UserEndpoints.FOLLOWLIST).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Set<User> result = mapper.readValue(followlist, new TypeReference<Set<User>>() {
        });
        Assert.assertEquals(follows, result);

    }

    @Test
    public void getPointsTodayTest() throws Exception {
        Log yesterday = new Log();
        yesterday.setDate(LocalDate.now().minusDays(1));
        yesterday.setAction(Action.VEGETARIAN);
        yesterday.setUser(currentUser);
        logRepository.save(yesterday);
        Log today = new Log();
        today.setDate(LocalDate.now());
        today.setAction(Action.VEGETARIAN);
        today.setUser(currentUser);
        logRepository.save(today);

        String result = this.mvc.perform(
                get(UserEndpoints.TODAYPROGRESS).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        int points = objectMapper.readValue(result, Integer.class);

        Assert.assertEquals(today.getPoints(), points);
    }

    @Test
    public void unfollowTest() throws Exception {
        User follow1 = new User();
        follow1.setUsername("follow1");
        follow1.setPassword("follow1");
        follow1.setEmail("follow1@gmail.com");
        String toRemove = this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow1)))
                .andReturn().getResponse().getContentAsString();

        User follow2 = new User();
        follow2.setUsername("follow2");
        follow2.setPassword("follow2");
        follow2.setEmail("follow2@gmail.com");
        this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow2)));

        this.mvc.perform(post(UserEndpoints.FOLLOW).contentType(MediaType.APPLICATION_JSON)
                .content("follow2").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk());

        this.mvc.perform(post(UserEndpoints.FOLLOW).contentType(MediaType.APPLICATION_JSON)
                .content("follow1").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk());


        String removed = this.mvc.perform(post(UserEndpoints.UNFOLLOW).contentType(MediaType.APPLICATION_JSON)
                .content(toRemove).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assert.assertEquals(follow1.getUsername(),
                objectMapper.readValue(removed, User.class).getUsername());
    }

    @Test
    public void noUsersFoundSearchTest() throws Exception {
        String list = this.mvc.perform(post(UserEndpoints.SEARCH).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content("stuff"))
                .andReturn().getResponse().getContentAsString();
        User[] response = objectMapper.readValue(list, User[].class);

        Assert.assertTrue(response.length == 0);
    }

    @Test
    public void getLeaderBoardTest() throws Exception {
        User follow1 = new User();
        follow1.setUsername("follow1");
        follow1.setPassword("follow1");
        follow1.setEmail("follow1@gmail.com");
        this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow1)))
                .andReturn().getResponse().getContentAsString();

        User follow2 = new User();
        follow2.setUsername("follow2");
        follow2.setPassword("follow2");
        follow2.setEmail("follow2@gmail.com");
        this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow2)));

        String listResponse = this.mvc.perform(get(UserEndpoints.LEADERBOARD)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andReturn().getResponse().getContentAsString();

        User[] list = objectMapper.readValue(listResponse, User[].class);
        Assert.assertEquals(list.length, 3);
        for (User check : list) {
            Assert.assertEquals(check.getPassword(), "");
        }
    }


    @Test
    public void getOtherPointsTest() throws Exception {
        User follow1 = new User();
        follow1.setUsername("follow1");
        follow1.setPassword("follow1");
        follow1.setEmail("follow1@gmail.com");
        this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow1)))
                .andReturn().getResponse().getContentAsString();

        User follow2 = new User();
        follow2.setUsername("follow2");
        follow2.setPassword("follow2");
        follow2.setEmail("follow2@gmail.com");
        this.mvc.perform(post(UserEndpoints.SIGNUP).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(follow2)));

        Log follow1Log = new Log();
        follow1Log.setAction(Action.VEGETARIAN);
        follow1Log.setUser(userRepository.findUserByUsername(follow1.getUsername()));
        follow1Log.setDate(LocalDate.now());
        logRepository.save(follow1Log);
        String points1 = this.mvc.perform(post(UserEndpoints.GETOTHERUSERPOINTS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(follow1.getUsername())).andReturn().getResponse().getContentAsString();

        String points2 = this.mvc.perform(post(UserEndpoints.GETOTHERUSERPOINTS).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(follow2.getUsername())).andReturn().getResponse().getContentAsString();

        Assert.assertEquals(Integer.parseInt(points1), Action.VEGETARIAN.getPoints());
        Assert.assertEquals(Integer.parseInt(points2), 0);
    }

    @After
    public void cleanup() {
        logRepository.deleteAll();
        userRepository.deleteAll();
    }
}
