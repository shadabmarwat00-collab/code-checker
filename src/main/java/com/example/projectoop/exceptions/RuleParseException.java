package com.example.projectoop.exceptions;

public class RuleParseException extends Exception {

    private String ruleName;

    public RuleParseException(String message, String ruleName) {
        super(message);
        this.ruleName = ruleName;
    }

    public RuleParseException(String message, String ruleName, Throwable cause) {
        super(message, cause);
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Override
    public String toString() {
        return "RuleParseException: " + getMessage() + " | Rule: " + ruleName;
    }
}