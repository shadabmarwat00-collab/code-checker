package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.ArrayList;
import java.util.List;

public class MethodLengthRule extends QualityRule {

    private static final int MAX_LINES = 20;

    public MethodLengthRule() {
        super("Method Length",
                "Methods should not exceed " + MAX_LINES + " lines",
                Violation.MEDIUM);
    }

    @Override
    public List<Violation> analyze(String fileContent) throws AnalysisFailureException {
        List<Violation> violations = new ArrayList<>();
        try {
            String[] lines = fileContent.split("\n");
            int methodStartLine = -1;
            int braceDepth      = 0;
            boolean insideMethod = false;
            String methodSignature = "";

            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();

                boolean isMethodStart = trimmed.contains("(")
                        && trimmed.contains(")")
                        && trimmed.contains("{")
                        && !trimmed.startsWith("if")
                        && !trimmed.startsWith("for")
                        && !trimmed.startsWith("while")
                        && !trimmed.startsWith("switch")
                        && !trimmed.startsWith("try")
                        && !trimmed.startsWith("catch")
                        && !trimmed.startsWith("class");

                if (isMethodStart && !insideMethod) {
                    insideMethod      = true;
                    methodStartLine   = i + 1;
                    methodSignature   = trimmed;
                    braceDepth        = 0;
                }

                if (insideMethod) {
                    for (char c : trimmed.toCharArray()) {
                        if (c == '{') braceDepth++;
                        if (c == '}') braceDepth--;
                    }
                    if (braceDepth <= 0 && insideMethod) {
                        int methodLength = (i + 1) - methodStartLine;
                        if (methodLength > MAX_LINES) {
                            // Truncate snippet if too long
                            String snippet = methodSignature.length() > 60
                                    ? methodSignature.substring(0, 60) + "..."
                                    : methodSignature;
                            violations.add(new Violation(
                                    name, methodStartLine,
                                    "Method is " + methodLength + " lines long"
                                            + " (max: " + MAX_LINES + ")",
                                    severity,
                                    snippet,
                                    "Break this method into smaller methods, each doing one thing. "
                                            + "Aim for methods under 20 lines. Extract repeated logic "
                                            + "into helper methods."
                            ));
                        }
                        insideMethod = false;
                    }
                }
            }
        } catch (Exception e) {
            throw new AnalysisFailureException(
                    "MethodLengthRule failed: " + e.getMessage(), name, e);
        }
        return violations;
    }
}
