package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import shared.endpoints.UserEndpoints;
import shared.models.Log;

@Service("LogService")
public class LogService {

    private RestTemplate restTemplate;

    @Autowired
    public LogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean createLog(Log log) {
        Log response = restTemplate.postForObject(UserEndpoints.LOGS, log, Log.class);
        return response != null;
    }
}
