package server.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Badges;
import shared.models.Log;
import shared.models.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class BadgeController {

    private LogRepository logRepository;
    private UserRepository userRepository;

    /**
     * The method computes and returns a list with a user's badges and their levels.
     *
     * @param auth to identify the user.
     * @return the list of badges and their levels.
     */
    @GetMapping(value = UserEndpoints.BADGES)
    public Badges[] getBadgeLevel(Authentication auth) {

        // Init total count and temp count
        int vegCount = 0;
        int tmpVegCount = 0;

        int localCount = 0;
        int tmpLocalCount = 0;

        int bikeCount = 0;
        int tmpBikeCount = 0;

        int publicCount = 0;
        int tmpPublicCount = 0;

        int tempCount = 0;
        int tmpTempCount = 0;

        // Put all solar logs into a array so we can check it later.
        ArrayList<Log> solarLogs = new ArrayList<>();

        // Keep track of previous log per type
        Log prevVeg = null;
        Log prevLocal = null;
        Log prevBike = null;
        Log prevPublic = null;
        Log prevTemperature = null;

        User user = userRepository.findUserByUsername(auth.getName());
        List<Log> logs = logRepository.findByUser(user);

        // This looks like it can be simplified, do not see how though.
        for (Log log : logs) {
            switch (log.getAction()) {
                case VEGETARIAN:
                    if (areConsecutive(prevVeg, log)) {
                        vegCount = Math.max(++tmpVegCount, vegCount);
                    } else {
                        tmpVegCount = 0;
                    }
                    prevVeg = log;
                    break;
                case LOCAL:
                    if (areConsecutive(prevLocal, log)) {
                        localCount = Math.max(++tmpLocalCount, localCount);
                    } else {
                        tmpLocalCount = 0;
                    }
                    prevLocal = log;
                    break;
                case TEMP:
                    if (areConsecutive(prevTemperature, log)) {
                        tempCount = Math.max(++tmpTempCount, tempCount);
                    } else {
                        tmpTempCount = 0;
                    }
                    prevTemperature = log;
                    break;
                case BIKE:
                    if (areConsecutive(prevBike, log)) {
                        bikeCount = Math.max(++tmpBikeCount, bikeCount);
                    } else {
                        tmpBikeCount = 0;
                    }
                    prevBike = log;
                    break;
                case PUBLIC:
                    if (areConsecutive(prevPublic, log)) {
                        publicCount = Math.max(++tmpPublicCount, publicCount);
                    } else {
                        tmpBikeCount = 0;
                    }
                    prevPublic = log;
                    break;
                case SOLAR:
                    //add all the solar logs to a separate array
                    solarLogs.add(log);
                    break;
                default:
                    break;
            }
        }

        Badges solar = Badges.Solar;
        setSolarLevel(solarLogs, solar);

        Badges vegetarian = Badges.Vegetarian;
        vegetarian.calculateAndSetLevel(vegCount);

        Badges bike = Badges.Bike;
        bike.calculateAndSetLevel(bikeCount);

        Badges publicTransport = Badges.Public;
        publicTransport.calculateAndSetLevel(publicCount);

        Badges temperature = Badges.Temp;
        temperature.calculateAndSetLevel(tempCount);

        Badges local = Badges.Local;
        local.setLevel(localCount);

        return new Badges[]{
                vegetarian,
                local,
                bike,
                publicTransport,
                solar
        };
    }

    private boolean areConsecutive(Log prev, Log next) {
        // Make it null safe
        if (prev == null || next == null || prev.getDate() == null || next.getDate() == null) {
            return true;
        }

        Period between = Period.between(prev.getDate(), next.getDate());
        return between.getDays() <= 1;
    }

    private void setSolarLevel(ArrayList<Log> list, Badges badge) {
        int count = 0;
        if (list.isEmpty()) {
            badge.setLevel(0);
            return;
        }

        for (int i = 0; i < list.size(); i += 2) {
            int days = Period.between(list.get(i).getDate(), list.get(i + 1).getDate()).getDays();
            count = Math.max(count, days);
        }

        if (list.size() % 2 != 0) {
            int days = Period.between(list.get(list.size() - 1).getDate(),
                    LocalDate.now()).getDays();
            count = Math.max(count, days);
        }
        badge.calculateAndSetLevel(count);
    }
}
