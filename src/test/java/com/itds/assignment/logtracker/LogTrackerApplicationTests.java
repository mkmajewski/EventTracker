package com.itds.assignment.logtracker;

import com.itds.assignment.logtracker.dto.EventEntryDto;
import com.itds.assignment.logtracker.mapper.EventMapper;
import com.itds.assignment.logtracker.model.Event;
import com.itds.assignment.logtracker.model.EventState;
import com.itds.assignment.logtracker.model.EventType;
import com.itds.assignment.logtracker.repository.EventRepository;
import com.itds.assignment.logtracker.service.EventServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
public class LogTrackerApplicationTests {

    @Mock
    private EventMapper eventMapper;

    private EventMapper eventMapperInst = new EventMapper();

    @InjectMocks
    private EventServiceImpl eventService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void given_EventEntryDtoList_when_ToManyEntries_then_ThrowsIllegalArgumentException() {
        EventEntryDto eventEntryDto1 = EventEntryDto.builder()
                .build();

        EventEntryDto eventEntryDto2 = EventEntryDto.builder()
                .build();

        EventEntryDto eventEntryDto3 = EventEntryDto.builder()
                .build();

        eventMapperInst.map(Arrays.asList(eventEntryDto1, eventEntryDto2, eventEntryDto3));
    }

    @Test
    public void given_EventEntryDtoList_when_Correct_then_ReturnEvent() {
        EventEntryDto eventEntryDto1 = EventEntryDto.builder()
                .id("12345")
                .state(EventState.FINISHED)
                .timestamp(1568919492l)
                .type(EventType.APPLICATION_LOG)
                .host("12345")
                .build();

        EventEntryDto eventEntryDto2 = EventEntryDto.builder()
                .id("12345")
                .state(EventState.STARTED)
                .timestamp(1568910482l)
                .type(EventType.APPLICATION_LOG)
                .host("12345")
                .build();

        Long duration = eventEntryDto1.getTimestamp() - eventEntryDto2.getTimestamp();

        eventMapperInst.setAlertTrigger("4000");
        Event event = eventMapperInst.map(Arrays.asList(eventEntryDto1, eventEntryDto2));

        assertEquals(eventEntryDto1.getState(), event.getState());
        assertEquals(duration, event.getDuration());
        assertEquals(true, event.getAlert());
    }

    @Test
    public void given_EventEntryLogFile_when_IsCorrect_then_ParseAndSaveIntoDatabase() throws IOException {
        File logFile = temporaryFolder.newFile("eventEntryLog.txt");
        FileWriter fw = new FileWriter(logFile);
        fw.write("{\"id\": \"test1\", \"state\": \"STARTED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377495212}\n" +
                "{\"id\": \"test2\", \"state\": \"STARTED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377495212}\n" +
                "{\"id\": \"test1\", \"state\": \"FINISHED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377499000}\n" +
                "{\"id\": \"test2\", \"state\": \"FINISHED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377599000}\n" +
                "{\"id\": \"test3\", \"state\": \"FINISHED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377599000}\n" +
                "{\"id\": \"test3\", \"state\": \"STARTED\", \"type\": \"APPLICATION_LOG\", \"host\": \"12345\", \"timestamp\": 1491377599000}");

        fw.close();

        List<Event> events = ReflectionTestUtils.invokeMethod(eventService,
                "readEventEntries", logFile.getAbsolutePath()
        );

        assertEquals(3, events.size());
    }

}
