package server.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.repositories.LogRepository;

import shared.models.Log;

import java.util.Date;

@RestController
@AllArgsConstructor
public class LogController {
    LogRepository logRepository;

    /**
     * The method which saves a new log to the database.
     * @param log The log which is to be saved.
     * @return Returns the modified log the the request
     */
    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public Log logAction(@RequestBody Log log) {
        log.setDate(new Date());

        logRepository.save(log); // Save to database

        return log;
    }
}
