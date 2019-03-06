package client;

import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
public class TestAppConfig {

    private AppConfig config = new AppConfig();

    @Test
    public void testRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = config.restTemplate(builder);

        Assert.isInstanceOf(RestTemplate.class, restTemplate);
    }
}
