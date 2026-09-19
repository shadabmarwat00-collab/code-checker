package com.example.projectoop.rules;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.*;
import java.util.regex.*;

public class NamingConventionRule extends QualityRule {

    private static final Set<String> BAD_NAMES = new HashSet<>(Arrays.asList(
            "a","b","c","d","x","y","z",
            "temp","tmp","data","obj","object",
            "val","value","var","variable","foo","bar"
    ));

    private static final Pattern VAR_PATTERN = Pattern.compile(
            "\\b(?:int|double|float|long|String|boolean|char|Object|var)\\s+(\\w+)\\s*[=;]"
    );

    public NamingConventionRule() {
        super("Naming Convention",
                "Variables should have meaningful descriptive names",
                Violation.MEDIUM);
    }

    @Override
    public List<Violation> analyze(String fileContent) throws AnalysisFailureException {
        List<Violation> violations = new ArrayList<>();
        try {
            String[] lines = fileContent.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.startsWith("//") || trimmed.startsWith("*")) continue;

                Matcher matcher = VAR_PATTERN.matcher(trimmed);
                while (matcher.find()) {
                    String varName = matcher.group(1);
                    if (BAD_NAMES.contains(varName.toLowerCase())) {
                        String snippet = trimmed.length() > 60
                                ? trimmed.substring(0, 60) + "..."
                                : trimmed;
                        violations.add(new Violation(
                                name, i + 1,
                                "Variable '" + varName + "' has a meaningless name",
                                severity,
                                snippet,
                                "Replace '" + varName + "' with a descriptive name that "
                                        + "explains what it stores. For example: instead of 'temp' "
                                        + "use 'totalPrice', instead of 'x' use 'studentCount'."
                        ));
                    }
                }
            }
        } catch (Exception e) {
            throw new AnalysisFailureException(
                    "NamingConventionRule failed: " + e.getMessage(), name, e);
        }
        return violations;
    }
}
