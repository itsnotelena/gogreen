package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.HelloWorld;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HelloWorldControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getRequestTest() throws Exception {
        String json = this.mvc.perform(
            get("/hello"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        HelloWorld convertJson = new ObjectMapper().readValue(json, HelloWorld.class);
        Assert.assertEquals(convertJson, new HelloWorld(1, "Hello World!"));
    }
}
