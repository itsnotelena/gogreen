package client.services;

import client.AppConfig;
import client.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Log;
import shared.models.User;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class TestLogService {
    @Autowired
    private LogService logService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private String url = AppConfig.getRootUri();

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testCreateLog() {
        Log log = new Log();
        log.setAction(Action.VEGETARIAN);
        mockServer.expect(requestTo(url + UserEndpoints.LOGS))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withNoContent());

        logService.createLog(log);

        mockServer.verify();
    }

    @Test
    public void createLogWrongUserTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Log log = new Log();
        log.setAction(Action.VEGETARIAN);
        mockServer.expect(requestTo(url + UserEndpoints.LOGS))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(log)));

        boolean toCompare = logService.createLog(log);

        mockServer.verify();

        Assert.assertTrue(toCompare);
    }
}
