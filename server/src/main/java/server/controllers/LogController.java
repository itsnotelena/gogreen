package server.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.repositories.LogRepository;
import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Log;
import shared.models.User;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class LogController {

    LogRepository logRepository;
    UserRepository userRepository;

    /**
     * The method which saves a new log to the database.
     *
     * @param log The log which is to be saved.
     * @return Returns the log the the request for confirmation (Date is modified though)
     */
    @RequestMapping(value = UserEndpoints.LOGS, method = RequestMethod.POST)
    public Log logAction(@RequestBody Log log, Authentication authentication) {
        log.setDate(LocalDate.now());
        User user = userRepository.findUserByUsername(authentication.getName());
        log.setUser(user);
        logRepository.save(log); // Save to database
        return log;
    }
}
