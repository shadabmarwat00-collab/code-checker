package com.example.projectoop.services;

import com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import com.example.projectoop.rules.*;
import java.util.ArrayList;
import java.util.List;

public class RuleEngine {

    private List<QualityRule> rules;

    public RuleEngine() {
        rules = new ArrayList<>();
        loadDefaultRules();
    }

    // Load all 5 rules
    private void loadDefaultRules() {
        rules.add(new MethodLengthRule());
        rules.add(new NestingDepthRule());
        rules.add(new NamingConventionRule());
        rules.add(new NullCheckRule());
        rules.add(new DuplicateLogicRule());
    }

    // Run all rules against the file content
    public List<Violation> runAllRules(String fileContent) throws AnalysisFailureException {
        List<Violation> allViolations = new ArrayList<>();
        for (QualityRule rule : rules) {
            try {
                List<Violation> ruleViolations = rule.analyze(fileContent);
                allViolations.addAll(ruleViolations);
            } catch (AnalysisFailureException e) {
                // Chained — rethrow with context
                throw new AnalysisFailureException(
                        "RuleEngine failed on rule: " + rule.getName(), rule.getName(), e);
            }
        }
        return allViolations;
    }

    public List<QualityRule> getRules() { return rules; }
    public int getRuleCount()           { return rules.size(); }
}

