package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class NullCheckRule extends QualityRule {

    private static final Pattern METHOD_PATTERN = Pattern.compile(
            "(?:public|private|protected)\\s+\\w+\\s+(\\w+)\\s*\\(([^)]+)\\)\\s*\\{"
    );

    private static final List<String> PRIMITIVES = List.of(
            "int","double","float","long","boolean","char","byte","short"
    );

    public NullCheckRule() {
        super("Null Check",
                "Methods accepting object parameters should validate for null",
                Violation.HIGH);
    }

    @Override
    public List<Violation> analyze(String fileContent) throws AnalysisFailureException {
        List<Violation> violations = new ArrayList<>();
        try {
            String[] lines = fileContent.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                Matcher m = METHOD_PATTERN.matcher(trimmed);
                if (!m.find()) continue;

                String params     = m.group(2);
                String methodName = m.group(1);
                boolean hasObjectParam = false;

                for (String param : params.split(",")) {
                    String type = param.trim().split("\\s+")[0];
                    if (!PRIMITIVES.contains(type)) { hasObjectParam = true; break; }
                }
                if (!hasObjectParam) continue;

                StringBuilder body = new StringBuilder();
                int depth = 0;
                for (int j = i; j < lines.length; j++) {
                    body.append(lines[j]);
                    for (char c : lines[j].toCharArray()) {
                        if (c == '{') depth++;
                        if (c == '}') depth--;
                    }
                    if (depth <= 0 && j > i) break;
                }

                if (!body.toString().contains("null")
                        && !body.toString().contains("Objects.requireNonNull")) {
                    String snippet = trimmed.length() > 60
                            ? trimmed.substring(0, 60) + "..."
                            : trimmed;
                    violations.add(new Violation(
                            name, i + 1,
                            "Method '" + methodName + "' never checks parameters for null",
                            severity,
                            snippet,
                            "Add a null check at the start of '" + methodName + "'. "
                                    + "Example:\n  if (" + params.trim().split("\\s+")[1] + " == null)"
                                    + "\n    throw new IllegalArgumentException(\"Parameter cannot be null\");"
                    ));
                }
            }
        } catch (Exception e) {
            throw new AnalysisFailureException(
                    "NullCheckRule failed: " + e.getMessage(), name, e);
        }
        return violations;
    }
}
