package server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.UserExistsException;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Action;
import shared.models.Badges;
import shared.models.Log;
import shared.models.SolarState;
import shared.models.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@AllArgsConstructor
public class UserController {

    private static final AtomicLong counter = new AtomicLong();

    private final UserRepository repository;

    private final LogRepository logRepository;

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

    /**
     * Creates and returns a user with the given username and password.
     *
     * @return the created user
     */
    @PostMapping(value = UserEndpoints.SIGNUP)
    public User createUser(@RequestBody User user) throws UserExistsException {
        user.setPassword(hashPassword(user.getPassword())); // Hash the password

        // Catch duplicate exception
        try {
            repository.save(user); // Save the user to the database
        } catch (DataIntegrityViolationException e) {
            throw new UserExistsException();
        }
        user.setPassword(""); // Don't leak the (even the hashed) password
        try {
            System.out.println(new ObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * The method returns how many points a user has according to the logs.
     *
     * @param authentication takes a user by which the log repository is sorted.
     * @return user points.
     */
    @GetMapping(value = UserEndpoints.ACTIONLIST)
    public int actionList(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        user.setPassword("");
        int points = 0;
        List<Log> list = logRepository.findByUser(user);
        if (list == null) {
            return 0;
        }
        for (Log log : list) {
            if (!log.getAction().equals(Action.SOLAR)) {
                points = points + log.getAction().getPoints();
            }
        }
        return points + getStateSolar(authentication).getPoints();
    }

    /**
     * The method returns a list of logs of a user to be displayed on the main screen.
     *
     * @param authentication to identify user.
     * @return the list of user logs.
     */
    @GetMapping(value = UserEndpoints.LOGS)
    public List<Log> getLogs(Authentication authentication) {
        User user = repository.findUserByUsername(authentication.getName());
        return logRepository.findByUser(user);
    }

    /**
     * Returns the state of the solar panels.
     *
     * @param authentication authentication details pof the user
     * @return an array representing a pair of the state of the button
     *     and the amount of points gathered by the solar panels.
     */
    @GetMapping(value = "/solar")
    public SolarState getStateSolar(Authentication authentication) {
        int points = 0;
        int total = 0;
        Log lastLog = null;
        for (Log log : getLogs(authentication)) {
            if (log.getAction().equals(Action.SOLAR)) {
                total++;
                if (total % 2 == 1) {
                    lastLog = log;
                } else {
                    LocalDate dateLatest = LocalDate.ofInstant(log.getDate().toInstant(),
                            ZoneId.systemDefault());
                    LocalDate datePrevious = LocalDate.ofInstant(lastLog.getDate().toInstant(),
                            ZoneId.systemDefault());
                    points += Action.SOLAR.getPoints()
                            * Period.between(datePrevious, dateLatest).getDays();
                }
            }
        }
        if (total % 2 == 1) {
            LocalDate datePrevious = LocalDate.ofInstant(lastLog.getDate().toInstant(),
                    ZoneId.systemDefault());
            points += Action.SOLAR.getPoints()
                    * Period.between(datePrevious, LocalDate.now()).getDays();
        }
        return new SolarState(points, total % 2 == 1);
    }

    /**
     * The method computes and returns a list with a user's badges and their levels.
     *
     * @param auth to identify the user.
     * @return the list of badges and their levels.
     */
    @GetMapping(value = UserEndpoints.BADGES)
    public List<Badges> getBadgeLevel(Authentication auth) {
        User user = repository.findUserByUsername(auth.getName());

        int vegetarianCount = 0;
        int localCount = 0;
        int bikeCount = 0;
        int publicCount = 0;
        int temperatureCount = 0;

        // Putting all solar logs into this array so i can use this in another method.
        ArrayList<Log> solars = new ArrayList<>();

        Log prevVeg = null;
        Log prevLocal = null;
        Log prevBike = null;
        Log prevPublic = null;
        Log prevTemperature = null;

        List<Log> logs = logRepository.findByUser(user);

        Badges vegetarian = Badges.Vegetarian;
        Badges bike = Badges.Bike;
        Badges publicTransport = Badges.Public;
        Badges temperature = Badges.Temp;
        Badges local = Badges.Local;

        for (Log log : logs) {
            switch (log.getAction()) {

                case VEGETARIAN:
                    counts(prevVeg, log, vegetarianCount, vegetarian);
                    break;
                case LOCAL:
                    counts(prevLocal, log, localCount, local);
                    break;
                case TEMP:
                    counts(prevTemperature, log, temperatureCount, temperature);
                    break;
                case BIKE:
                    counts(prevBike, log, bikeCount, bike);
                    break;
                case PUBLIC:
                    counts(prevPublic, log, publicCount, publicTransport);
                    break;
                case SOLAR:
                    //add all the solar logs to a separate array
                    solars.add(log);
                    break;
                default:
                    break;
            }
        }

        Badges solar = Badges.Solar;
        solarBadgeLevel(solars, solar);

        List<Badges> badges = new ArrayList<>();
        badges.add(vegetarian);
        badges.add(local);
        badges.add(temperature);
        badges.add(bike);
        badges.add(publicTransport);
        badges.add(solar);

        return badges;
    }

    /**
     * Method checks if two consecutive logs have consecutive days
     *     and if yes, increments the counter which will decide the badge level.
     * @param previous log is before the current log.
     * @param current current log.
     * @param counter counter which will be incremented.
     * @param badge the badge to set a level
     */
    public void counts(Log previous, Log current, int counter, Badges badge) {
        //set level immediately before we lost the value of counter
        setBadgeLevel(badge, counter);
        //if previous is null, set previous to current
        if (previous == null) {
            previous = current;
        } else {
            // else if the previous is not null then get the date of the previous
            LocalDate datePrevious = LocalDate.ofInstant(previous.getDate().toInstant(),
                    ZoneId.systemDefault());
            // get the date of the current
            LocalDate dateNow = LocalDate.ofInstant(current.getDate().toInstant(),
                    ZoneId.systemDefault());
            //if previous + 1 day equals date now then increment the counter
            if (dateNow.equals(datePrevious.plusDays(1))) {
                counter++;
            } else {
                //else set the counter back to 0 because we start counting from the beginning.
                counter = 0;
            }
        }
    }

    /**
     * Method calculates level of badge level for solar panels according to
     *     turned on/turned off logs.
     * @param list takes a list of only solar logs.
     * @param badge takes a badge and sets its level.
     */
    public void solarBadgeLevel(ArrayList<Log> list, Badges badge) {
        int result = 0;
        //edge case, if list is empty then just return
        if (list.isEmpty()) {
            return;
        }
        //check if the list size is even, that means the button is switched off.
        if (list.size() % 2 == 0) {
            //for every 2 check their dates
            for (int i = 1; i < list.size(); i = i + 2) {
                int variable = 0;
                LocalDate datePrevious = LocalDate.ofInstant(list.get(variable).getDate().toInstant(),
                        ZoneId.systemDefault());

                // get the date of the current
                LocalDate dateNow = LocalDate.ofInstant(list.get(i).getDate().toInstant(),
                        ZoneId.systemDefault());

                if (result > badge.getLevel()) {
                    checkSolarDates(datePrevious, dateNow, result);
                }
                variable = variable + 2;
            }
        } else {
            //check for every 2 apart from the last one (which is enabled)
            for (int i = 1; i < list.size() - 1; i = i + 2) {
                int variable = 0;
                LocalDate datePrevious = LocalDate.ofInstant(list.get(variable).getDate().toInstant(),
                        ZoneId.systemDefault());

                // get the date of the current
                LocalDate dateNow = LocalDate.ofInstant(list.get(i).getDate().toInstant(),
                        ZoneId.systemDefault());
                if (result > badge.getLevel()) {
                    checkSolarDates(datePrevious, dateNow, result);
                }
                variable = variable + 2;
            }

            //check how long has it been enabled until the present date
            LocalDate datePrevious = LocalDate.ofInstant(
                    list.get(list.size() - 1).getDate().toInstant(),
                    ZoneId.systemDefault());
            LocalDate dateNow = LocalDate.now(ZoneId.systemDefault());

            if (dateNow.equals(datePrevious.plusDays(3))) {
                result = 3;
            }
            if (dateNow.equals(datePrevious.plusDays(7))) {
                result = 7;
            }
            if (dateNow.equals(datePrevious.plusDays(28))) {
                result = 28;
            }
            setBadgeLevel(badge, result);
        }

    }

    /**
     * Method sets the badge level according to the counter.
     * @param badge the badge which level is going to be set.
     * @param counter according to it the level is set.
     */
    public void setBadgeLevel(Badges badge, int counter) {
        if (badge.getLevel() < counter) {
            if (counter == 3) {
                badge.setLevel(1);
            }
            if (counter == 7) {
                badge.setLevel(2);
            }
            if (counter == 28) {
                badge.setLevel(3);
            }
        }
    }

    public void checkSolarDates(LocalDate prev, LocalDate now, int counter){
        if (prev.plusDays(3).equals(now)){
            counter = 3;
        }
        if (prev.plusDays(7).equals(now)){
            counter = 7;
        }
        if (prev.plusDays(28).equals(now)){
            counter = 28;
        }
    }
}
