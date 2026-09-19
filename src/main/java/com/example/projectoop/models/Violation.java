package com.example.projectoop.models;

import java.io.Serializable;

public class Violation implements Comparable<Violation>, Serializable {

    public static final int LOW    = 1;
    public static final int MEDIUM = 2;
    public static final int HIGH   = 3;

    private String ruleName;
    private int    lineNumber;
    private String message;
    private int    severity;
    private String codeSnippet;
    private String fixSuggestion;

    public Violation(String ruleName, int lineNumber, String message,
                     int severity, String codeSnippet, String fixSuggestion) {
        this.ruleName      = ruleName;
        this.lineNumber    = lineNumber;
        this.message       = message;
        this.severity      = severity;
        this.codeSnippet   = codeSnippet != null ? codeSnippet.trim() : "";
        this.fixSuggestion = fixSuggestion != null ? fixSuggestion : "";
    }

    public Violation(String ruleName, int lineNumber,
                     String message, int severity) {
        this(ruleName, lineNumber, message, severity, "", "");
    }

    public String getRuleName()      { return ruleName;      }
    public int    getLineNumber()    { return lineNumber;     }
    public String getMessage()       { return message;        }
    public int    getSeverity()      { return severity;       }
    public String getCodeSnippet()   { return codeSnippet;   }
    public String getFixSuggestion() { return fixSuggestion; }

    public String getSeverityLabel() {
        switch (severity) {
            case HIGH:   return "HIGH";
            case MEDIUM: return "MEDIUM";
            default:     return "LOW";
        }
    }

    @Override
    public int compareTo(Violation other) {
        return Integer.compare(other.severity, this.severity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Violation)) return false;
        Violation other = (Violation) obj;
        return this.lineNumber == other.lineNumber
                && this.ruleName.equals(other.ruleName);
    }

    @Override
    public int hashCode() {
        return 31 * ruleName.hashCode() + lineNumber;
    }

    @Override
    public String toString() {
        return "[" + getSeverityLabel() + "] Line " + lineNumber
                + " | " + ruleName + ": " + message;
    }
}
