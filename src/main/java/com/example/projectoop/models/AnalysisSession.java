package com.example.projectoop.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalysisSession implements Serializable, Cloneable {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String        fileName;
    private String        developerName;
    private LocalDateTime timestamp;
    private List<Violation> violations;
    private BigDecimal    qualityScore;  // 0.00 to 100.00

    public AnalysisSession(String fileName, String developerName) {
        this.fileName      = fileName;
        this.developerName = developerName;
        this.timestamp     = LocalDateTime.now();
        this.violations    = new ArrayList<>();
        this.qualityScore  = BigDecimal.ZERO;
    }

    // ── Violation management ─────────────────────────────────────────────
    public void addViolation(Violation v) {
        if (v != null && !violations.contains(v)) {
            violations.add(v);
        }
    }

    public void addAllViolations(List<Violation> list) {
        for (Violation v : list) addViolation(v);
    }

    public List<Violation> getViolations() {
        List<Violation> sorted = new ArrayList<>(violations);
        Collections.sort(sorted);  // uses Comparable — HIGH first
        return sorted;
    }

    public int getViolationCount()  { return violations.size(); }

    public int countBySeverity(int severity) {
        int count = 0;
        for (Violation v : violations)
            if (v.getSeverity() == severity) count++;
        return count;
    }

    // ── Score ────────────────────────────────────────────────────────────
    public void setQualityScore(BigDecimal score) {
        this.qualityScore = score.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getQualityScore() { return qualityScore; }

    public String getScoreGrade() {
        int score = qualityScore.intValue();
        if (score >= 90) return "A — Excellent";
        if (score >= 75) return "B — Good";
        if (score >= 60) return "C — Average";
        if (score >= 40) return "D — Needs Work";
        return "F — Poor";
    }

    // ── Getters ──────────────────────────────────────────────────────────
    public String        getFileName()      { return fileName;      }
    public String        getDeveloperName() { return developerName; }
    public LocalDateTime getTimestamp()     { return timestamp;     }
    public String        getFormattedTime() { return timestamp.format(FORMATTER); }

    // ── Clone ────────────────────────────────────────────────────────────
    @Override
    public AnalysisSession clone() {
        try {
            AnalysisSession copy = (AnalysisSession) super.clone();
            copy.violations = new ArrayList<>(this.violations);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    @Override
    public String toString() {
        return "Session[" + getFormattedTime() + "] "
                + fileName + " | Score: " + qualityScore
                + " | Violations: " + violations.size();
    }
}
