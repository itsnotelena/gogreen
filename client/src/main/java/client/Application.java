package client;

import org.springframework.web.client.RestTemplate;

public class Application {

    public static void main(String args[]) {
        RestTemplate restTemplate = new RestTemplate();
        Message message = restTemplate.getForObject("http://localhost:8080/hello", Message.class);
        System.out.println(message.toString());
    }
}
