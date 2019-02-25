package client;

import models.HelloWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HelloWorldService {

    private RestTemplate restTemplate;

    @Autowired
    public HelloWorldService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gets the hello world endpoint.
     *
     * @return the message from the endpoint
     */
    public String getHello() {
        HelloWorld message = restTemplate.getForObject("http://localhost:8080/hello", HelloWorld.class);
        if (message != null) {
            return message.toString();
        }
        return "";
    }
}
