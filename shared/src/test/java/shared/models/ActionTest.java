package shared.models;

import org.junit.Assert;
import org.junit.Test;

public class ActionTest {

    @Test
    public void stringVegetarian_Test() {
        Assert.assertEquals("ate a vegetarian meal", Action.VEGETARIAN.historyString());
    }

    @Test
    public void stringProduce_Test() {
        Assert.assertEquals("bought local produce", Action.LOCAL.historyString());
    }

    @Test
    public void stringTemperature_Test() {
        Assert.assertEquals("lowered the temperature in your home", Action.TEMP.historyString());
    }

    @Test
    public void stringSolar_Test() {
        Assert.assertEquals("installed solar panels", Action.SOLAR.historyString());
    }

    @Test
    public void stringBike_Test() {
        Assert.assertEquals("used the bike instead of a car", Action.BIKE.historyString());
    }

    @Test
    public void stringPublic_Test() {
        Assert.assertEquals("used public transport instead of a car", Action.PUBLIC.historyString());
    }
}
