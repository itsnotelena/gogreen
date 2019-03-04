package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import shared.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Test
    public void createUserTest() throws Exception {
        User testUser = new User();
        String username = "test";
        testUser.setUsername(username);

        String output = this.mvc.perform(
                post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"" + username + "\"}")
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User parsedOutput = new ObjectMapper().readValue(output, User.class);

        Assert.assertEquals(parsedOutput.getUsername(), username);
    }

    /**
     * Makes sure we don't return any password data
     */
    @Test
    public void emptyPassTest() throws Exception {
        String output = this.mvc.perform(
            post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"test\"}")
        )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        User parsedOutput = new ObjectMapper().readValue(output, User.class);

        Assert.assertEquals(parsedOutput.getPassword(), "");
    }
}
