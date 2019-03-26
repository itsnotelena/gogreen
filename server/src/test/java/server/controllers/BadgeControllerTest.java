package server.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
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
import shared.models.Badges;
import shared.models.Log;
import shared.models.User;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BadgeControllerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    private MockMvc mvc;

    private User testUser;

    private String UString;

    private String authorization;

    private ObjectMapper mapper;

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Before
    public void setup() throws Exception {
        logRepository.deleteAll();
        userRepository.deleteAll();

        String username = "test";
        testUser = new User();
        testUser.setUsername(username);
        testUser.setPassword(hashPassword("test"));
        UString = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + "test" + "\"}";

        // Remove the test user if it exists
        User toDelete = userRepository.findUserByUsername(username);
        if (toDelete != null) {
            userRepository.delete(toDelete);
        }

        userRepository.save(testUser);

        authorization = this.mvc.perform(
                post(UserEndpoints.LOGIN).contentType(MediaType.APPLICATION_JSON).content(UString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getHeader("Authorization");

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void noBadgeTest() throws Exception {
        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badges[] badges = mapper.readValue(response, Badges[].class);

        Badges[] testBadges = new Badges[]{
                Badges.Vegetarian,
                Badges.Local,
                Badges.Bike,
                Badges.Public,
                Badges.Solar
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @Test
    public void simpleBadgeTest() throws Exception {

        Log veg1 = new Log();
        veg1.setUser(testUser);
        veg1.setAction(Action.VEGETARIAN);
        veg1.setDate(LocalDate.EPOCH);
        logRepository.save(veg1);

        Log veg2 = new Log();
        veg2.setUser(testUser);
        veg2.setAction(Action.VEGETARIAN);
        veg2.setDate(LocalDate.EPOCH.plusDays(1));
        logRepository.save(veg2);

        Log veg3 = new Log();
        veg3.setUser(testUser);
        veg3.setAction(Action.VEGETARIAN);
        veg3.setDate(LocalDate.EPOCH.plusDays(2));
        logRepository.save(veg3);

        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badges[] badges = mapper.readValue(response, Badges[].class);

        Badges vegBadge = Badges.Vegetarian;
        vegBadge.setLevel(1);

        Badges[] testBadges = new Badges[]{
                vegBadge,
                Badges.Local,
                Badges.Bike,
                Badges.Public,
                Badges.Solar
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @Test
    public void simpleSolarTest() throws Exception {

        Log sol1 = new Log();
        sol1.setUser(testUser);
        sol1.setAction(Action.SOLAR);
        sol1.setDate(LocalDate.EPOCH);
        logRepository.save(sol1);

        Log sol2 = new Log();
        sol2.setUser(testUser);
        sol2.setAction(Action.SOLAR);
        sol2.setDate(LocalDate.EPOCH.plusDays(3));
        logRepository.save(sol2);


        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badges[] badges = mapper.readValue(response, Badges[].class);

        Badges solBadge = Badges.Solar;
        solBadge.setLevel(1);

        Badges[] testBadges = new Badges[]{
                Badges.Vegetarian,
                Badges.Local,
                Badges.Bike,
                Badges.Public,
                solBadge
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @After
    public void cleanup() {
        logRepository.deleteAll();
        userRepository.deleteAll();
    }
}
