package com.example.projectoop.exceptions;
public class AnalysisFailureException extends Exception {

    private String ruleName;

    public AnalysisFailureException(String message, String ruleName) {
        super(message);
        this.ruleName = ruleName;
    }

    // Chained exception - wraps original cause
    public AnalysisFailureException(String message, String ruleName, Throwable cause) {
        super(message, cause);
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Override
    public String toString() {
        return "AnalysisFailureException: " + getMessage() + " | Rule: " + ruleName;
    }
}