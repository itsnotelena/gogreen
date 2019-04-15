package server.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.User;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MockMvc mvc;
    private User testUser = new User();
    private User followUser = new User();

    private String UString;
    private String FString;
    private String authorization;

    @Before
    public void setup() {
        userRepository.deleteAll();

        String username = "test";
        testUser.setUsername(username);
        testUser.setPassword("test");
        testUser.setEmail("test@gmail.com");
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword()
                + "\", \"email\": \"" + testUser.getEmail() +"\"}";

        // Remove the test user if it exists
        User toDelete = userRepository.findUserByUsername(username);
        if (toDelete != null) {
            userRepository.delete(toDelete);
        }
    }

    @Test
    public void SignUpAndTest() throws Exception {
        // Creating the account
        String output = this.mvc.perform(
                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User parsedOutput = new ObjectMapper().readValue(output, User.class);

        Assert.assertEquals(parsedOutput.getUsername(), testUser.getUsername());
        Assert.assertEquals(parsedOutput.getPassword(), "");
    }

    @Test
    public void SignUpAndLoginTest() throws Exception {
        // Creating the account
        String output = this.mvc.perform(
                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User parsedOutput = new ObjectMapper().readValue(output, User.class);

        Assert.assertEquals(parsedOutput.getUsername(), testUser.getUsername());
        Assert.assertEquals(parsedOutput.getPassword(), "");

        // Logging in with the account
        String login = this.mvc.perform(
                post("/login").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getHeader("Authorization");

        Assert.assertNotNull(login);
        Assert.assertTrue(login.startsWith("Bearer "));
    }

    @Test
    public void DuplicateUserTest() throws Exception {
        // Creating the account
        int ok = this.mvc.perform(
                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        Assert.assertEquals(HttpStatus.OK.value(), ok);

        // Second time expect a conflict
        int conflict = this.mvc.perform(
                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse().getStatus();

        Assert.assertEquals(HttpStatus.CONFLICT.value(), conflict);
    }

    @Test
    public void invalidEmailSignUp() throws Exception{
        User toRegister = new User();
        toRegister.setUsername("toRegister");
        toRegister.setPassword("pass");
        toRegister.setEmail("invalid");
        String request = new ObjectMapper().writeValueAsString(toRegister);

        int response = this.mvc.perform(post(UserEndpoints.SIGNUP)
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getStatus();

        Assert.assertEquals(HttpStatus.CONFLICT.value(), response);
    }

    @Test
    public void passwordRecovery_userNotFoundTest() throws Exception {
        String email = "noUserWithThisEmailInTheDatabase@gmail.com";
        String response = this.mvc.perform(post(UserEndpoints.FORGOTPASSWORD)
                .contentType(MediaType.APPLICATION_JSON).content(email)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, "");
    }

    @Test
    public void passwordRecovery_okEmailTest() throws Exception {
        User user = new User();
        user.setUsername("oopp");
        user.setPassword("oopp");
        String email = "ooppgogreen@gmail.com";
        user.setEmail(email);
        String request = new ObjectMapper().writeValueAsString(user);
        this.mvc.perform(post(UserEndpoints.SIGNUP)
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse().getStatus();

        String response = this.mvc.perform(post(UserEndpoints.FORGOTPASSWORD)
                .contentType(MediaType.APPLICATION_JSON).content(email)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(response, email);
    }


    @After
    public void cleanUp() {
        userRepository.deleteAll();
    }
}
