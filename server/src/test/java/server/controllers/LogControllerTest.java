package server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LogControllerTest {
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
        testUser.setEmail("test@test.com");
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword()
                + "\", \"email\": \"" + testUser.getEmail() +"\"}";

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
    public void sendActionLogTest() throws Exception{
        ObjectMapper mapper =  new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Log req = new Log();
        req.setAction(Action.VEGETARIAN);
        String postContent = mapper.writeValueAsString(req);
        String response = this.mvc.perform(post(UserEndpoints.LOGS)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(postContent))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Log log = mapper.readValue(response,Log.class);

        Assert.assertEquals(log.getAction(), req.getAction());
        Assert.assertEquals(log.getUser().getUsername(), testUser.getUsername());

        logRepository.delete(log);
    }
}
