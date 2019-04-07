package shared.models;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

public class UserTest {

    @Test
    public void equals_same_objectTest() {
        User user1 = new User();
        User user2 = user1;
        Assert.assertEquals(user1, user2);
    }

    @Test
    public void equals_differentClassTest() {
        User user1 = new User();
        Action action = Action.TEMP;
        Assert.assertNotEquals(user1, action);
    }

    @Test
    public void equals_nullTest() {
        User user1 = new User();
        User user2 = null;
        Assert.assertNotEquals(user1, user2);
    }

    @Test
    public void equals_differentFollowTest() {
        User user1 = new User();
        user1.setUsername("test");
        User user2 = new User();
        user2.setUsername("test");
        HashSet<User> friends = new HashSet<>();
        friends.add(user2);
        user1.setFollowing(friends);
        Assert.assertEquals(user1, user2);
    }

    @Test
    public void emptyMailTest() {
        User user = new User();
        user.setEmail("");
        Assert.assertFalse(user.validateEmail());
    }
}
