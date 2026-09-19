package com.example.projectoop.exceptions;

public class CorruptProfileException extends Exception {

    private String developerName;

    public CorruptProfileException(String message, String developerName) {
        super(message);
        this.developerName = developerName;
    }

    public CorruptProfileException(String message, String developerName, Throwable cause) {
        super(message, cause);
        this.developerName = developerName;
    }

    public String getDeveloperName() {
        return developerName;
    }

    @Override
    public String toString() {
        return "CorruptProfileException: " + getMessage() + " | Developer: " + developerName;
    }
}