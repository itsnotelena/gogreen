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
import shared.models.SolarState;
import shared.models.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.Date;
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
        testUser.setEmail("test@test");
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
        logRepository.delete(new ObjectMapper().readValue(toDelete, Log.class));
    }

    @Test
    public void getSolarPoints_OnlyOneToggle_Test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Date datePrevious = Date.from(LocalDate.now().minus(Period.ofDays(2))
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
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
        logRepository.delete(test);
    }

    @Test
    public void getSolarPoints_afterOnOff_Test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
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
        logRepository.delete(mapper.readValue(toDelete1, Log.class));
        logRepository.delete(mapper.readValue(toDelete2, Log.class));
    }
}
