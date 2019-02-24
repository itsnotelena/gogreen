package client;

import org.springframework.web.client.RestTemplate;

import models.HelloWorld;


public class Application {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        HelloWorld message = restTemplate.getForObject("http://localhost:8080/hello", HelloWorld.class);
        if (message != null) {
            System.out.println("Message from the server\n" + message.toString() + "\n");
        }
    }
}
