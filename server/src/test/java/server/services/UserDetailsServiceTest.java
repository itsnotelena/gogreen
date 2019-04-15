package server.services;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import server.repositories.UserRepository;
import shared.models.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsServiceTest {

    @Autowired
    UserRepository userRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testLoadUserUsername() throws Exception {
        User user = new User();
        String usernameToFind = "notInDatabase";
        user.setPassword("pass");
        user.setUsername("user");
        user.setEmail("ooppgogreen@gmail.com");
        userRepository.save(user);
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage(usernameToFind);
        new UserDetailsServiceImpl(userRepository).loadUserByUsername(usernameToFind);
    }

}
