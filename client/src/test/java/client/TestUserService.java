//package client;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpMethod;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.web.client.RestTemplate;
//
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {AppConfig.class})
//public class TestUserService {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    private MockRestServiceServer mockServer;
//
//    private String url = AppConfig.getRootUri();
//
//    @Before
//    public void setUp() {
//        mockServer = MockRestServiceServer.createServer(restTemplate);
//    }
//
//    @Test
//    public void testWrongPath() {
//        mockServer.expect(requestTo(url + "/hello"))
//            .andExpect(method(HttpMethod.GET))
//            .andRespond(withNoContent());
//        mockServer.verify();
//    }
//}
