package shared.models;

import org.junit.Assert;
import org.junit.Test;

public class BadgeTest {

    @Test
    public void calculateMonthStreak_Test() {
        Badge badge = new Badge(BadgeType.Vegetarian);
        badge.calculateAndSetLevel(30);
        Assert.assertEquals(3, badge.getLevel());
    }

    @Test
    public void calculateWeekStreak_Test() {
        Badge badge = new Badge(BadgeType.Vegetarian);
        badge.calculateAndSetLevel(10);
        Assert.assertEquals(2, badge.getLevel());
    }

    @Test
    public void calculateDailyStreak_Test() {
        Badge badge = new Badge(BadgeType.Vegetarian);
        badge.calculateAndSetLevel(5);
        Assert.assertEquals(1, badge.getLevel());
    }

    @Test
    public void calculateNoStreak_Test() {
        Badge badge = new Badge(BadgeType.Vegetarian);
        badge.calculateAndSetLevel(0);
        Assert.assertEquals(0, badge.getLevel());
    }
}
