package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaxFitnessEvaluationsTest {

    @Test
    void testInitialProgressIsZero() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(10);
        stoppingCondition.notifySearchStarted();

        assertEquals(0.0, stoppingCondition.getProgress(),
                "Progress at the beginning should be 0.0");
    }

    @Test
    void testProgressAfterSomeEvaluations() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(10);
        stoppingCondition.notifySearchStarted();

        for (int i = 0; i < 4; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }

        assertEquals(0.4, stoppingCondition.getProgress(), 0.0001,
                "Progress after 4/10 evaluations should be 0.4");
    }

    @Test
    void testStopConditionNotReached() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(5);
        stoppingCondition.notifySearchStarted();

        stoppingCondition.notifyFitnessEvaluation(); // 1
        stoppingCondition.notifyFitnessEvaluation(); // 2

        assertFalse(stoppingCondition.searchMustStop(),
                "Stop condition should not be reached at 2/5 evaluations");
    }

    @Test
    void testStopConditionReachedExactLimit() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(3);
        stoppingCondition.notifySearchStarted();

        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();

        assertTrue(stoppingCondition.searchMustStop(),
                "Stop condition should be reached at exactly 3 evaluations");
    }

    @Test
    void testStopConditionExceededLimit() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(3);
        stoppingCondition.notifySearchStarted();

        for (int i = 0; i < 5; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }

        assertTrue(stoppingCondition.searchMustStop(),
                "Stop condition should still be true even when evaluations exceed max");
    }

    @Test
    void testProgressNeverExceedsOne() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(3);
        stoppingCondition.notifySearchStarted();

        for (int i = 0; i < 10; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }

        assertEquals(1.0, stoppingCondition.getProgress(),
                "Progress must not exceed 1.0 even if evaluations exceed limit");
    }

    @Test
    void testZeroMaxEvaluationsMeansImmediateStop() {
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(0);
        stoppingCondition.notifySearchStarted();

        assertTrue(stoppingCondition.searchMustStop(),
                "If max evaluations = 0, algorithm must immediately stop");

        assertEquals(1.0, stoppingCondition.getProgress(),
                "Progress should be 1.0 when max evaluations = 0");
    }
}
