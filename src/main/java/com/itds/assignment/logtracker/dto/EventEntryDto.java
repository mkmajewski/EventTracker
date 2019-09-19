package com.itds.assignment.logtracker.dto;

import com.itds.assignment.logtracker.model.EventState;
import com.itds.assignment.logtracker.model.EventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventEntryDto {
    private String id;

    private EventState state;

    private EventType type;

    private String host;

    private Long timestamp;
}
