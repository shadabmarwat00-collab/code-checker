package com.example.projectoop.services;



import com.example.projectoop.models.Violation;
import java.math.BigDecimal;
import java.util.List;

public interface Scorable {
    BigDecimal computeScore(List<Violation> violations);
}

