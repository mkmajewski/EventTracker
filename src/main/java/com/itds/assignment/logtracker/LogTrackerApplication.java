package com.itds.assignment.logtracker;

import com.itds.assignment.logtracker.model.Event;
import com.itds.assignment.logtracker.model.Message;
import com.itds.assignment.logtracker.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LogTrackerApplication implements CommandLineRunner {

    @Autowired
    private EventService eventService;
    public static void main(String[] args) {
        SpringApplication.run(LogTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length != 1) {
            log.error(Message.ERR_INVALID_NUMBER_OF_INPUT_ARGUMENTS.getMessage());
            System.exit(-1);
        }

        eventService.processLog(args[0]);

        listEvents(eventService.findEvents());
    }

    private void listEvents(List<Event> events) {
        log.info("------------- EVENTS --------------------");
        events.stream().forEach(e -> log.info(e.toString()));
        log.info("------------- END EVENTS ----------------");
    }
}
