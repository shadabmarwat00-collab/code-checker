package com.example.projectoop.exceptions;

public class InvalidFileException extends Exception {

    private String filePath;

    public InvalidFileException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }

    public InvalidFileException(String message, String filePath, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "InvalidFileException: " + getMessage() + " | File: " + filePath;
    }
}

