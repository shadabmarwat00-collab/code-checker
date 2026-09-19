package com.example.projectoop.services;

import com.example.projectoop.models.AnalysisSession;
import com.example.projectoop.models.Violation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportBuilder implements Reportable {

    private AnalysisSession session;

    public ReportBuilder(AnalysisSession session) {
        this.session = session;
    }

    @Override
    public String generateReport() {
        // Uses StringBuilder to build the full report
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("       CODE QUALITY FINGERPRINT REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("Developer : ").append(session.getDeveloperName()).append("\n");
        sb.append("File      : ").append(session.getFileName()).append("\n");
        sb.append("Date      : ").append(session.getFormattedTime()).append("\n");
        sb.append("───────────────────────────────────────────────────\n");
        sb.append("Quality Score : ").append(session.getQualityScore())
                .append(" / 100\n");
        sb.append("Grade         : ").append(session.getScoreGrade()).append("\n");
        sb.append("───────────────────────────────────────────────────\n");

        sb.append("VIOLATIONS FOUND: ").append(session.getViolationCount()).append("\n");
        sb.append("  HIGH   : ").append(session.countBySeverity(Violation.HIGH)).append("\n");
        sb.append("  MEDIUM : ").append(session.countBySeverity(Violation.MEDIUM)).append("\n");
        sb.append("  LOW    : ").append(session.countBySeverity(Violation.LOW)).append("\n");
        sb.append("───────────────────────────────────────────────────\n");

        sb.append("DETAILED VIOLATIONS:\n\n");
        List<Violation> violations = session.getViolations();
        if (violations.isEmpty()) {
            sb.append("  No violations found. Excellent code quality!\n");
        } else {
            for (int i = 0; i < violations.size(); i++) {
                sb.append("  ").append(i + 1).append(". ")
                        .append(violations.get(i).toString()).append("\n");
            }
        }

        sb.append("═══════════════════════════════════════════════════\n");
        return sb.toString();
    }

    @Override
    public void exportToFile(String path) {
        String report = generateReport();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(report);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to export report: " + e.getMessage());
        }
    }
}

