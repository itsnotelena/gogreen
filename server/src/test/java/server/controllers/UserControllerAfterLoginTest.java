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
import server.repositories.UserRepository;
import shared.models.Action;
import shared.models.User;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerAfterLoginTest {

    @Autowired
    UserRepository userRepository;
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
                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString));
        authorization = this.mvc.perform(
                post("/login").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getHeader("Authorization");

    }

    @Test
    public void sendActionTest() throws Exception {
        String postContent = new ObjectMapper().writeValueAsString(Action.VEGETARIAN);
        String result = this.mvc.perform(post("/action").header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(postContent))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertEquals(100, Integer.parseInt(result));
    }

    @Test
    public void getPointsNoActionTest() throws Exception {
        String result = this.mvc.perform(get("/points").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        Assert.assertEquals(0, Integer.parseInt(result));
    }

    @Test
    public void getPointsAfterActionTest() throws Exception {
        String postContent = new ObjectMapper().writeValueAsString(Action.VEGETARIAN);
        this.mvc.perform(post("/action").header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON).content(postContent))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        String result = this.mvc.perform(get("/points").header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Assert.assertEquals(100, Integer.parseInt(result));
    }
}
