package com.itds.assignment.logtracker.service;

import com.itds.assignment.logtracker.model.Event;

import java.util.List;

public interface EventService {
    void processLog(String eventEntriesLogPath);

    List<Event> findEvents();
}
