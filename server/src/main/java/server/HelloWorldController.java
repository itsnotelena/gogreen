package server;

import models.HelloWorld;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class HelloWorldController {
    private static final String hello = "Hello World!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/hello")
    public HelloWorld helloWorld() {
        return new HelloWorld(counter.incrementAndGet(), hello);
    }
}
