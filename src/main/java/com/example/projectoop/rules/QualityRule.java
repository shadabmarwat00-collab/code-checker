package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.List;

public abstract class QualityRule implements Analyzable {

    protected String name;
    protected String description;
    protected int    severity;  // 1=Low, 2=Medium, 3=High

    public QualityRule(String name, String description, int severity) {
        this.name        = name;
        this.description = description;
        this.severity    = severity;
    }

    // ── Abstract — every subclass must implement its own logic ───────────
    @Override
    public abstract List<Violation> analyze(String fileContent)
            throws AnalysisFailureException;

    // ── Getters ──────────────────────────────────────────────────────────
    public String getName()        { return name;        }
    public String getDescription() { return description; }
    public int    getSeverity()    { return severity;    }

    @Override
    public String toString() {
        return "Rule[" + name + "] Severity=" + severity;
    }
}

