package client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import shared.endpoints.UserEndpoints;
import shared.models.Badge;

@Service("BadgeService")
public class BadgeService {
    private final RestTemplate restTemplate;

    @Autowired
    public BadgeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Returns an array of the badge levels for the given User.
     * @return The array of badge levels.
     */
    public Badge[] getBadges() {

        ResponseEntity<Badge[]> response = restTemplate.exchange(UserEndpoints.BADGES,
                HttpMethod.GET, null, new ParameterizedTypeReference<Badge[]>(){});

        Badge[] badges = response.getBody();
        return badges;

    }

}
