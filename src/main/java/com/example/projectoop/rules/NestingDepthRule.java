package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.ArrayList;
import java.util.List;

public class NestingDepthRule extends QualityRule {

    private static final int MAX_DEPTH = 3;

    public NestingDepthRule() {
        super("Nesting Depth",
                "Code should not be nested deeper than " + MAX_DEPTH + " levels",
                Violation.HIGH);
    }

    @Override
    public List<Violation> analyze(String fileContent) throws AnalysisFailureException {
        List<Violation> violations = new ArrayList<>();
        try {
            String[] lines = fileContent.split("\n");
            int depth = 0;

            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.startsWith("//") || trimmed.startsWith("*")) continue;

                for (char c : trimmed.toCharArray()) {
                    if (c == '{') {
                        depth++;
                        if (depth > MAX_DEPTH) {
                            String snippet = trimmed.length() > 60
                                    ? trimmed.substring(0, 60) + "..."
                                    : trimmed;
                            violations.add(new Violation(
                                    name, i + 1,
                                    "Nesting depth " + depth
                                            + " at line " + (i + 1)
                                            + " (max allowed: " + MAX_DEPTH + ")",
                                    severity,
                                    snippet,
                                    "Reduce nesting by using early returns (guard clauses), "
                                            + "extracting nested blocks into separate methods, "
                                            + "or using the ternary operator for simple conditions."
                            ));
                            break;
                        }
                    }
                    if (c == '}') { depth--; if (depth < 0) depth = 0; }
                }
            }
        } catch (Exception e) {
            throw new AnalysisFailureException(
                    "NestingDepthRule failed: " + e.getMessage(), name, e);
        }
        return violations;
    }
}
