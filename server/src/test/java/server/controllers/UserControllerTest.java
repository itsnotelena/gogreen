package server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.After;
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
        String username = "test";
        testUser.setUsername(username);
        testUser.setPassword("test");
        testUser.setEmail("test@test");
        String followUsername = "follow";
        followUser.setUsername(followUsername);
        followUser.setPassword("follow");
        followUser.setEmail("follow@follow");

        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword()
                + "\", \"email\": \"" + testUser.getEmail() + "\"}";
        FString = "{\"username\": \"" + followUser.getUsername() + "\", \"password\": \"" + followUser.getPassword()
                + "\", \"email\": \"" + followUser.getEmail() + "\"}";

        // Remove the test user if it exists
        User toDelete = userRepository.findUserByUsername(username);
        if (toDelete != null) {
            userRepository.delete(toDelete);
        }

    }

//    @After("execution(* test.java.server.controllers.UserControllerTest.SignUpAndLogin(..))")
//    public void signUpFollow() throws Exception {
//        //creat account for follower
//        String output = this.mvc.perform(
//                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(FString))
//                //.andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        System.out.println(output);
//
//        //get the auth from follower
//        authorization = this.mvc.perform(
//                post("/login").contentType(MediaType.APPLICATION_JSON).content(FString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse().getHeader("Authorization");
//
//        // Creating the account for test user
//        String out = this.mvc.perform(
//                post("/user/signup").contentType(MediaType.APPLICATION_JSON).content(UString))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        // Logging in with the account test user
//        String auth = this.mvc.perform(
//                post("/login").contentType(MediaType.APPLICATION_JSON).content(UString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse().getHeader("Authorization");
//    }

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
    public void SignUpAndLogin() throws Exception {
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

//        @Test
//                public void UserEndPointTest() throws Exception {
//        //Check 'to follow' endpoint
//        int result = this.mvc.perform(
//                post(UserEndpoints.FOLLOW).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .content(testUser.getUsername()))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(result, HttpStatus.OK.value());
//
//        //Check followees list endpoint
//        int out = this.mvc.perform(
//                get(UserEndpoints.FOLLOWLIST).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .contentType(MediaType.APPLICATION_JSON).content(FString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(out, HttpStatus.OK.value());
//
//        //Check search endpoint
//        int check = this.mvc.perform(
//                post(UserEndpoints.SEARCH).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .content(testUser.getUsername()))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(check, HttpStatus.OK.value());
//
//        //Check 'to unfollow' endpoint
//        int status = this.mvc.perform(
//                post(UserEndpoints.UNFOLLOW).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .contentType(MediaType.APPLICATION_JSON).content(UString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(status, HttpStatus.OK.value());
//
//        //Check leaderboard endpoint
//        int Status = this.mvc.perform(
//                get(UserEndpoints.LEADERBOARD).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .contentType(MediaType.APPLICATION_JSON).content(FString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(Status, HttpStatus.OK.value());
//    }

//    @Test
//            public void GetActionPoints() throws Exception {
//        int OK = this.mvc.perform(
//                get(UserEndpoints.ACTIONLIST).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .contentType(MediaType.APPLICATION_JSON).content(FString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(OK, HttpStatus.OK.value());
//    }
//
//    @Test
//            public void GetOtherUserPoints() throws Exception {
//        //Get other users' points
//        int ok = this.mvc.perform(
//                post(UserEndpoints.GETOTHERUSERPOINTS).header(HttpHeaders.AUTHORIZATION, authorization)
//                        .contentType(MediaType.APPLICATION_JSON).content(FString))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getStatus();
//
//        Assert.assertEquals(ok, HttpStatus.OK.value());
//    }

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
}
