package com.itds.assignment.logtracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Message {
    ERR_INVALID_NUMBER_OF_INPUT_ARGUMENTS("Invalid number of input arguments. Single argument (path to logfile) expected."),
    ERR_FILE_PROCESSING_ERROR("File processing error occured."),
    ERR_INCORRECT_EVENT_ENTRIES_NUMBER_IN_FILE("Incorrect event entries in file. Each event should have at most two entries.");

    private String message;
}
