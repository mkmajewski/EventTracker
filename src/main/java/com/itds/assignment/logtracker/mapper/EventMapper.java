package com.itds.assignment.logtracker.mapper;

import com.itds.assignment.logtracker.dto.EventEntryDto;
import com.itds.assignment.logtracker.model.Event;
import com.itds.assignment.logtracker.model.EventState;
import com.itds.assignment.logtracker.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private String alertTrigger;

    @Value("${application.alert-trigger}")
    public void setAlertTrigger(String alertTrigger) {
        this.alertTrigger = alertTrigger;
    }

    public Event map(List<EventEntryDto> eventEntries) {
        if (CollectionUtils.isEmpty(eventEntries) || eventEntries.size() > 2) {
            throw new IllegalArgumentException(Message.ERR_INCORRECT_EVENT_ENTRIES_NUMBER_IN_FILE.getMessage());
        }

        Long duration = getDuration(eventEntries);
        eventEntries = eventEntries.stream().sorted(Comparator.comparingLong(EventEntryDto::getTimestamp).reversed()).collect(Collectors.toList());

        Event event = Event.builder()
                .host(eventEntries.get(0).getHost())
                .identifier(eventEntries.get(0).getId())
                .type(eventEntries.get(0).getType())
                .state(getEventState(eventEntries))
                .duration(duration)
                .alert(duration > Long.valueOf(alertTrigger) ? true : false)
                .build();

        return event;
    }

    private EventState getEventState(List<EventEntryDto> eventEntries) {
        return eventEntries.stream().findFirst().get()
                .getState();
    }

    private Long getDuration(List<EventEntryDto> eventEntries) {
        if (eventEntries.size() == 1) {
            return 0l;
        } else {
            return eventEntries.stream().map(EventEntryDto::getTimestamp)
                    .reduce((t1, t2) -> t1 - t2).get();
        }
    }
}
