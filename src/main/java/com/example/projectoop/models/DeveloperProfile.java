package com.example.projectoop.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeveloperProfile implements Serializable {

    private String            developerName;
    private List<AnalysisSession> sessions;

    public DeveloperProfile(String developerName) {
        this.developerName = developerName;
        this.sessions      = new ArrayList<>();
    }

    // ── Session management ───────────────────────────────────────────────
    public void addSession(AnalysisSession session) {
        if (session != null) sessions.add(session);
    }

    public List<AnalysisSession> getSessions() { return sessions; }
    public int getTotalSessions()              { return sessions.size(); }
    public String getDeveloperName()           { return developerName;   }

    // ── Average score across all sessions ───────────────────────────────
    public BigDecimal getAverageScore() {
        if (sessions.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (AnalysisSession s : sessions)
            total = total.add(s.getQualityScore());
        return total.divide(
                new BigDecimal(sessions.size()), 2, RoundingMode.HALF_UP);
    }

    // ── Growth trend: compare last two sessions ──────────────────────────
    public String getGrowthTrend() {
        if (sessions.size() < 2) return "Not enough sessions yet";
        BigDecimal last    = sessions.get(sessions.size() - 1).getQualityScore();
        BigDecimal previous= sessions.get(sessions.size() - 2).getQualityScore();
        int cmp = last.compareTo(previous);
        if (cmp > 0) return "Improving ▲";
        if (cmp < 0) return "Declining ▼";
        return "Stable ●";
    }

    // ── Top 3 recurring weaknesses ───────────────────────────────────────
    // Counts how many times each rule was violated across ALL sessions
    public List<String> getTopWeaknesses() {
        Map<String, Integer> ruleCount = new HashMap<>();
        for (AnalysisSession s : sessions) {
            for (Violation v : s.getViolations()) {
                String rule = v.getRuleName();
                ruleCount.put(rule, ruleCount.getOrDefault(rule, 0) + 1);
            }
        }

        // Sort by frequency descending, take top 3
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(ruleCount.entrySet());
        entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> top = new ArrayList<>();
        for (int i = 0; i < Math.min(3, entries.size()); i++) {
            Map.Entry<String, Integer> e = entries.get(i);
            top.add(e.getKey() + " (" + e.getValue() + " times)");
        }
        if (top.isEmpty()) top.add("No weaknesses detected yet");
        return top;
    }

    // ── Total violations across all sessions ─────────────────────────────
    public int getTotalViolations() {
        int total = 0;
        for (AnalysisSession s : sessions) total += s.getViolationCount();
        return total;
    }

    // ── Last session ─────────────────────────────────────────────────────
    public AnalysisSession getLastSession() {
        if (sessions.isEmpty()) return null;
        return sessions.get(sessions.size() - 1);
    }

    @Override
    public String toString() {
        return "DeveloperProfile[" + developerName + "] "
                + "Sessions: " + sessions.size()
                + " | Avg Score: " + getAverageScore()
                + " | Trend: " + getGrowthTrend();
    }
}
