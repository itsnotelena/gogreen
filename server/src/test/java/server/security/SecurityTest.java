package server.security;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import shared.models.User;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mvc;

    private User testUser = new User();

    private String UString;

    @Before
    public void setup() {
        String username = "test" + (Math.random() * 10);
        testUser.setUsername(username);
        testUser.setPassword("test");
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + testUser.getPassword() + "\"}";
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
    public void testConstants() {
        SecurityConstants constants = new SecurityConstants();
        Assert.assertNotNull(constants);
    }

}
