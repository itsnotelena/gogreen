package models;

import org.junit.Assert;
import org.junit.Test;

public class HelloWorldTest {

    @Test
    public void equalsDifferentTest() {
        HelloWorld firstResponse = new HelloWorld(1, "Hello World");
        HelloWorld secondResponse = new HelloWorld(2, "Hello World");
        Assert.assertNotEquals(firstResponse,secondResponse);
    }

    @Test
    public void equalsSameTest() {
        HelloWorld first = new HelloWorld(1, "Hello World");
        HelloWorld second = new HelloWorld(1, "Hello World");
        Assert.assertEquals(first, first);
        Assert.assertEquals(first, second);
    }

    @Test
    public void toStringTest() {
        HelloWorld hello = new HelloWorld(42, "gibberish");
        Assert.assertEquals("gibberish", hello.toString());
    }

    @Test
    public void constructorTest() {
        HelloWorld hello = new HelloWorld(1, "Hello World");
        Assert.assertEquals(1,hello.getId());
        Assert.assertEquals("Hello World", hello.getContent());
    }
}
