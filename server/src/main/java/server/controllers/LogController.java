package server.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import server.repositories.LogRepository;

import server.repositories.UserRepository;
import shared.endpoints.UserEndpoints;
import shared.models.Log;
import shared.models.User;

import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
public class LogController {

    LogRepository logRepository;
    UserRepository userRepository;

    /**
     * The method which saves a new log to the database.
     * @param log The log which is to be saved.
     * @return Returns the log the the request for confirmation (Date is modified though)
     */
    @RequestMapping(value = UserEndpoints.POSTLOG, method = RequestMethod.POST)
    public Log logAction(@RequestBody Log log, Authentication authentication) {
        log.setDate(new Date());
        User user = userRepository.findUserByUsername(authentication.getName());
        log.setUser(user);
        logRepository.save(log); // Save to database
        return log;
    }


}
