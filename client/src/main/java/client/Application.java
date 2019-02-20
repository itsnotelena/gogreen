package client;

import org.springframework.web.client.RestTemplate;

import java.io.Console;


public class Application {

    public static void main(String args[]) {
        RestTemplate restTemplate = new RestTemplate();
        Message message = restTemplate.getForObject("http://localhost:8080/hello", Message.class);
        Console cons = System.console();
        cons.printf("Message from the server\n" + message.toString() + "\n");
    }
}
