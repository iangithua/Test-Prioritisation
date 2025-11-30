package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class SimulatedAnnealingTest {

    // Generates a simple TestOrder instance for the search process
    static class MockOrderGenerator implements EncodingGenerator<TestOrder> {
        @Override
        public TestOrder get() {
            int[] defaultOrder = {0, 1, 2, 3, 4};
            return new TestOrder(new ShiftToBeginningMutation(new Random()), defaultOrder);
        }
    }

    // Basic energy evaluator used to measure solution "quality"
    static class MockEnergyEvaluator implements FitnessFunction<TestOrder> {
        @Override
        public double applyAsDouble(TestOrder candidate) {
            int total = 0;
            for (int element : candidate.getPositions()) {
                total += element;
            }
            return total; // Larger sums = higher energy = worse
        }

        @Override
        public double maximise(TestOrder candidate) {
            return applyAsDouble(candidate);
        }

        @Override
        public double minimise(TestOrder candidate) {
            return -applyAsDouble(candidate);
        }
    }

    // Simple condition that limits the number of evaluations
    static class MockStopCondition implements StoppingCondition {
        private int evalCount = 0;
        private final int limit;

        public MockStopCondition(int limit) {
            this.limit = limit;
        }

        @Override
        public void notifySearchStarted() {
            evalCount = 0;
        }

        @Override
        public void notifyFitnessEvaluation() {
            evalCount++;
        }

        @Override
        public boolean searchMustStop() {
            return evalCount >= limit;
        }

        @Override
        public double getProgress() {
            return (double) evalCount / limit;
        }
    }

    @Test
    void testFindSolution() {
        int maxSteps = 10;

        MockStopCondition condition = new MockStopCondition(maxSteps);
        MockOrderGenerator generator = new MockOrderGenerator();
        MockEnergyEvaluator evaluator = new MockEnergyEvaluator();
        Random rng = new Random();

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                condition, generator, evaluator, 1, rng);

        TestOrder result = sa.findSolution();

        assertNotNull(result, "Returned solution should not be null.");
        assertTrue(result.size() > 0, "Returned solution must not be empty.");
    }

    @Test
    void testWorseSolutionIsAccepted() {
        int stepLimit = 1;
        MockStopCondition condition = new MockStopCondition(stepLimit);
        MockOrderGenerator generator = new MockOrderGenerator();
        MockEnergyEvaluator evaluator = new MockEnergyEvaluator();

        // RNG always returns 0 → ensures SA accepts worse moves
        Random forcedAcceptRng = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }
        };

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                condition, generator, evaluator, 1, forcedAcceptRng);

        TestOrder outcome = sa.findSolution();

        assertNotNull(outcome);
    }

    @Test
    void testNoImprovementScenario() {
        int stepLimit = 1;
        MockStopCondition condition = new MockStopCondition(stepLimit);
        MockOrderGenerator generator = new MockOrderGenerator();
        MockEnergyEvaluator evaluator = new MockEnergyEvaluator();

        // RNG always returns 1 → ensures SA never accepts worse moves
        Random forcedRejectRng = new Random() {
            @Override
            public double nextDouble() {
                return 1.0;
            }
        };

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                condition, generator, evaluator, 1, forcedRejectRng);

        TestOrder outcome = sa.findSolution();

        assertNotNull(outcome);
    }
}
