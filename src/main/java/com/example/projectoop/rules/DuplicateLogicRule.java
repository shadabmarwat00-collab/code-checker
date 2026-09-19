package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.*;

public class DuplicateLogicRule extends QualityRule {

    private static final int MIN_LINE_LENGTH  = 15;
    private static final int DUPLICATE_THRESHOLD = 2;

    public DuplicateLogicRule() {
        super("Duplicate Logic",
                "Repeated code blocks should be extracted into reusable methods",
                Violation.MEDIUM);
    }

    @Override
    public List<Violation> analyze(String fileContent) throws AnalysisFailureException {
        List<Violation> violations = new ArrayList<>();
        try {
            String[] lines = fileContent.split("\n");
            Map<String, List<Integer>> lineOccurrences = new HashMap<>();

            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.length() < MIN_LINE_LENGTH) continue;
                if (trimmed.startsWith("//"))     continue;
                if (trimmed.startsWith("*"))      continue;
                if (trimmed.startsWith("import")) continue;
                if (trimmed.startsWith("package"))continue;
                if (trimmed.equals("{") || trimmed.equals("}")) continue;

                lineOccurrences
                        .computeIfAbsent(trimmed, k -> new ArrayList<>())
                        .add(i + 1);
            }

            for (Map.Entry<String, List<Integer>> entry : lineOccurrences.entrySet()) {
                if (entry.getValue().size() >= DUPLICATE_THRESHOLD) {
                    int firstLine = entry.getValue().get(0);
                    String code   = entry.getKey();
                    String preview = code.length() > 55
                            ? code.substring(0, 55) + "..." : code;
                    violations.add(new Violation(
                            name, firstLine,
                            "Duplicate code appears " + entry.getValue().size()
                                    + " times (lines: " + entry.getValue() + ")",
                            severity,
                            preview,
                            "Extract this repeated code into a separate method and call "
                                    + "that method wherever it is needed. This follows the "
                                    + "DRY principle (Don't Repeat Yourself)."
                    ));
                }
            }
        } catch (Exception e) {
            throw new AnalysisFailureException(
                    "DuplicateLogicRule failed: " + e.getMessage(), name, e);
        }
        return violations;
    }
}
