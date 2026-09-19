package com.example.projectoop.services;

import com.example.projectoop.models.Violation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ScoreCalculator implements Scorable {

    // Penalty deducted per violation based on severity
    private static final BigDecimal HIGH_PENALTY   = new BigDecimal("8.00");
    private static final BigDecimal MEDIUM_PENALTY = new BigDecimal("4.00");
    private static final BigDecimal LOW_PENALTY    = new BigDecimal("2.00");
    private static final BigDecimal MAX_SCORE      = new BigDecimal("100.00");

    @Override
    public BigDecimal computeScore(List<Violation> violations) {
        BigDecimal totalPenalty = BigDecimal.ZERO;

        for (Violation v : violations) {
            switch (v.getSeverity()) {
                case Violation.HIGH:
                    totalPenalty = totalPenalty.add(HIGH_PENALTY);
                    break;
                case Violation.MEDIUM:
                    totalPenalty = totalPenalty.add(MEDIUM_PENALTY);
                    break;
                default:
                    totalPenalty = totalPenalty.add(LOW_PENALTY);
            }
        }

        BigDecimal score = MAX_SCORE.subtract(totalPenalty);

        // Score cannot go below 0
        if (score.compareTo(BigDecimal.ZERO) < 0) score = BigDecimal.ZERO;

        return score.setScale(2, RoundingMode.HALF_UP);
    }
}
