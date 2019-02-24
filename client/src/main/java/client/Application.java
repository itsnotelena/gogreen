package client;

import org.springframework.stereotype.Service;

@Service
public class Application {

    public static void main(String[] args) {
        HelloWorldService helloWorldService = new HelloWorldService();
        System.out.println(helloWorldService.getHello());
    }
}
