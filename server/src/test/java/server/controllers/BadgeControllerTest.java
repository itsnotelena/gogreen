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
import shared.models.Badge;
import shared.models.BadgeType;
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

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge[] testBadges = new Badge[]{
                new Badge(BadgeType.Vegetarian),
                new Badge(BadgeType.Local),
                new Badge(BadgeType.Bike),
                new Badge(BadgeType.Public),
                new Badge(BadgeType.Solar),
                new Badge(BadgeType.Temp)
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

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge vegBadge = new Badge(BadgeType.Vegetarian);
        vegBadge.setLevel(1);

        Badge[] testBadges = new Badge[]{
                vegBadge,
                new Badge(BadgeType.Local),
                new Badge(BadgeType.Bike),
                new Badge(BadgeType.Public),
                new Badge(BadgeType.Solar),
                new Badge(BadgeType.Temp),
        };

        Assert.assertArrayEquals(testBadges, badges);

        testBadges[0] = new Badge(BadgeType.Vegetarian);
        Assert.assertNotEquals(testBadges, badges);
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

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge solBadge = new Badge(BadgeType.Solar);
        solBadge.setLevel(1);

        Badge[] testBadges = new Badge[]{
                new Badge(BadgeType.Vegetarian),
                new Badge(BadgeType.Local),
                new Badge(BadgeType.Bike),
                new Badge(BadgeType.Public),
                solBadge,
                new Badge(BadgeType.Temp)
        };

        Assert.assertArrayEquals(testBadges, badges);
        solBadge.setLevel(2);
        Assert.assertNotEquals(testBadges, badges);
    }

    @Test
    public void solarStillEnabled() throws Exception {

        Log sol1 = new Log();
        sol1.setUser(testUser);
        sol1.setAction(Action.SOLAR);
        sol1.setDate(LocalDate.now().minusDays(30));
        logRepository.save(sol1);

        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge solBadge = new Badge(BadgeType.Solar);
        solBadge.setLevel(3);

        Badge[] testBadges = new Badge[]{
                new Badge(BadgeType.Vegetarian),
                new Badge(BadgeType.Local),
                new Badge(BadgeType.Bike),
                new Badge(BadgeType.Public),
                solBadge,
                new Badge(BadgeType.Temp),
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @Test
    public void twoBadgesTest() throws Exception {

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

        Log bike1 = new Log();
        bike1.setUser(testUser);
        bike1.setAction(Action.BIKE);
        bike1.setDate(LocalDate.EPOCH.plusDays(0));
        logRepository.save(bike1);

        Log bike2 = new Log();
        bike2.setUser(testUser);
        bike2.setAction(Action.BIKE);
        bike2.setDate(LocalDate.EPOCH.plusDays(1));
        logRepository.save(bike2);

        Log veg3 = new Log();
        veg3.setUser(testUser);
        veg3.setAction(Action.VEGETARIAN);
        veg3.setDate(LocalDate.EPOCH.plusDays(2));
        logRepository.save(veg3);

        Log bike3 = new Log();
        bike3.setUser(testUser);
        bike3.setAction(Action.BIKE);
        bike3.setDate(LocalDate.EPOCH.plusDays(2));
        logRepository.save(bike3);

        Log bike4uncon = new Log();
        bike4uncon.setUser(testUser);
        bike4uncon.setAction(Action.BIKE);
        bike4uncon.setDate(LocalDate.EPOCH.plusDays(4));
        logRepository.save(bike4uncon);

        Log veg4uncon = new Log();
        veg4uncon.setUser(testUser);
        veg4uncon.setAction(Action.VEGETARIAN);
        veg4uncon.setDate(LocalDate.EPOCH.plusDays(5));
        logRepository.save(veg4uncon);

        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge vegBadge = new Badge(BadgeType.Vegetarian);
        vegBadge.setLevel(1);

        Badge bikeBadge = new Badge(BadgeType.Bike);
        bikeBadge.setLevel(1);

        Badge[] testBadges = new Badge[]{
                vegBadge,
                new Badge(BadgeType.Local),
                bikeBadge,
                new Badge(BadgeType.Public),
                new Badge(BadgeType.Solar),
                new Badge(BadgeType.Temp)
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @Test
    public void extendedTest() throws Exception {

        Log local1 = new Log();
        local1.setUser(testUser);
        local1.setAction(Action.LOCAL);
        local1.setDate(LocalDate.EPOCH.plusDays(0));
        logRepository.save(local1);

        Log temp0 = new Log();
        temp0.setUser(testUser);
        temp0.setAction(Action.TEMP);
        temp0.setDate(LocalDate.EPOCH.plusDays(0));
        logRepository.save(temp0);

        Log local2 = new Log();
        local2.setUser(testUser);
        local2.setAction(Action.LOCAL);
        local2.setDate(LocalDate.EPOCH.plusDays(1));
        logRepository.save(local2);

        Log local3 = new Log();
        local3.setUser(testUser);
        local3.setAction(Action.LOCAL);
        local3.setDate(LocalDate.EPOCH.plusDays(2));
        logRepository.save(local3);

        Log temp1 = new Log();
        temp1.setUser(testUser);
        temp1.setAction(Action.TEMP);
        temp1.setDate(LocalDate.EPOCH.plusDays(3));
        logRepository.save(temp1);

        Log temp2 = new Log();
        temp2.setUser(testUser);
        temp2.setAction(Action.TEMP);
        temp2.setDate(LocalDate.EPOCH.plusDays(4));
        logRepository.save(temp2);

        Log local4 = new Log();
        local4.setUser(testUser);
        local4.setAction(Action.LOCAL);
        local4.setDate(LocalDate.EPOCH.plusDays(4));
        logRepository.save(local4);

        Log public1 = new Log();
        public1.setUser(testUser);
        public1.setAction(Action.PUBLIC);
        public1.setDate(LocalDate.EPOCH.plusDays(4));
        logRepository.save(public1);

        Log public2 = new Log();
        public2.setUser(testUser);
        public2.setAction(Action.PUBLIC);
        public2.setDate(LocalDate.EPOCH.plusDays(5));
        logRepository.save(public2);


        Log temp3 = new Log();
        temp3.setUser(testUser);
        temp3.setAction(Action.TEMP);
        temp3.setDate(LocalDate.EPOCH.plusDays(5));
        logRepository.save(temp3);

        Log public3 = new Log();
        public3.setUser(testUser);
        public3.setAction(Action.PUBLIC);
        public3.setDate(LocalDate.EPOCH.plusDays(6));
        logRepository.save(public3);

        Log public4 = new Log();
        public4.setUser(testUser);
        public4.setAction(Action.PUBLIC);
        public4.setDate(LocalDate.EPOCH.plusDays(9));
        logRepository.save(public4);

        String response = this.mvc.perform(get(UserEndpoints.BADGES)
                .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Badge[] badges = mapper.readValue(response, Badge[].class);

        Badge lBadge = new Badge(BadgeType.Local);
        lBadge.setLevel(1);

        Badge tBadge = new Badge(BadgeType.Temp);
        lBadge.setLevel(1);

        Badge pBadge = new Badge(BadgeType.Public);
        pBadge.setLevel(1);

        Badge[] testBadges = new Badge[]{
                new Badge(BadgeType.Vegetarian),
                lBadge,
                new Badge(BadgeType.Bike),
                pBadge,
                new Badge(BadgeType.Solar),
                tBadge
        };

        Assert.assertArrayEquals(testBadges, badges);
    }

    @After
    public void cleanup() {
        logRepository.deleteAll();
        userRepository.deleteAll();
    }
}
