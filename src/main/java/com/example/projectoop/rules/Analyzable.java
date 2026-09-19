package com.example.projectoop.rules;

import  com.example.projectoop.exceptions.AnalysisFailureException;
import com.example.projectoop.models.Violation;
import java.util.List;

public interface Analyzable {
    List<Violation> analyze(String fileContent) throws AnalysisFailureException;
}
