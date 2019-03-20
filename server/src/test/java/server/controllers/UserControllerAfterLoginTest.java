package server.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.User;

import java.util.List;


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

    private String authorization;

    @Before
    public void setup() throws Exception{
        String username = "test";
        testUser.setUsername(username);
        testUser.setPassword("test");
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword() + "\"}";

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

    }


    @Test
    public void noLogUserTest() throws Exception {
        String response = this.mvc.perform(get(UserEndpoints.LOGS).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertTrue(new ObjectMapper().readValue(response, List.class).isEmpty());
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
        String toDelete = this.mvc.perform(post(UserEndpoints.POSTLOG).header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(postContent))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        String result = this.mvc.perform(get(UserEndpoints.ACTIONLIST).header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertEquals(Action.VEGETARIAN.getPoints(), Integer.parseInt(result));
        logRepository.delete(new ObjectMapper().readValue(toDelete, Log.class));
    }
}
