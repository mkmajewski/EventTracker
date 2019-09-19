package com.itds.assignment.logtracker.service;

import com.google.gson.Gson;
import com.itds.assignment.logtracker.dto.EventEntryDto;
import com.itds.assignment.logtracker.mapper.EventMapper;
import com.itds.assignment.logtracker.model.Event;
import com.itds.assignment.logtracker.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Override
    public void processLog(String eventEntriesLogPath) {
        if (StringUtils.isEmpty(eventEntriesLogPath)) {
            throw new IllegalArgumentException();
        }

        try {
            saveEvents(readEventEntries(eventEntriesLogPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findEvents() {
        return eventRepository.findAll();
    }

    private List<Event> saveEvents(List<Event> events) {
        return eventRepository.saveAll(events);
    }

    private List<Event> readEventEntries(String eventEntriesLogPath) throws IOException {
        Gson g = new Gson();
        try (Stream<String> stream = Files.lines(Paths.get(eventEntriesLogPath))) {
            return stream.map(l -> g.fromJson(l, EventEntryDto.class))
                    .collect(Collectors.groupingBy(EventEntryDto::getId))
                    .entrySet()
                    .parallelStream()
                    .map(es -> eventMapper.map(es.getValue()))
                    .collect(Collectors.toList());
        }
    }
}
