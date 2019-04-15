package shared.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    User user1 = new User();
    User user2 = new User();

    @Test
    public void equals() {
        user1.setUsername("user");
        user2.setUsername("test");
        assertFalse(user1.getUsername().equals(user2.getUsername()));
        user2.setUsername("user");
        assertTrue(user1.getUsername().equals(user2.getUsername()));
        assertTrue(user1.equals(user2));
        user2 = null;
        assertFalse(user1.equals(user2));
        Log log = new Log();
        assertFalse(user1.equals(log));
    }

    @Test
    public void validateEmail() {
        user1.setEmail("email");
        assertFalse(user1.validateEmail());
        user1.setEmail("@email");
        assertFalse(user1.validateEmail());
        user1.setEmail("email@");
        assertFalse(user1.validateEmail());
        user1.setEmail("");
        assertFalse(user1.validateEmail());
        user1.setEmail("email@email");
        assertFalse(user1.validateEmail());
        user1.setEmail("email@email.com");
        assertTrue(user1.validateEmail());
        user1.setEmail("");
        assertFalse(user1.validateEmail());
    }
}