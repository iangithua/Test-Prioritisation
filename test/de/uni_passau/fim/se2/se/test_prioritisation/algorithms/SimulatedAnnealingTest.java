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

    // Provides a deterministic TestOrder instance for SA to operate on.
    static class TestOrderGenerator implements EncodingGenerator<TestOrder> {
        @Override
        public TestOrder get() {
            int[] initial = {0, 1, 2, 3, 4};  // simple sequence for testing
            return new TestOrder(new ShiftToBeginningMutation(new Random()), initial);
        }
    }

    // Very simple energy function: total sum of positions is the energy.
    static class TestEnergyFunction implements FitnessFunction<TestOrder> {
        @Override
        public double applyAsDouble(TestOrder encoding) {
            int sum = 0;
            for (int v : encoding.getPositions()) sum += v;
            return sum;
        }

        @Override
        public double maximise(TestOrder encoding) {
            return applyAsDouble(encoding);
        }

        @Override
        public double minimise(TestOrder encoding) {
            return -applyAsDouble(encoding); // lower = better
        }
    }

    // Tracks how many evaluations have been performed.
    static class TestStoppingCondition implements StoppingCondition {
        private int evaluations = 0;
        private final int maxEvaluations;

        public TestStoppingCondition(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
        }

        @Override
        public void notifySearchStarted() {
            evaluations = 0;
        }

        @Override
        public void notifyFitnessEvaluation() {
            evaluations++;
        }

        @Override
        public boolean searchMustStop() {
            return evaluations >= maxEvaluations;
        }

        @Override
        public double getProgress() {
            return (double) evaluations / maxEvaluations;
        }
    }


    @Test
    void testFindSolution() {
        TestStoppingCondition stop = new TestStoppingCondition(10);
        TestOrderGenerator generator = new TestOrderGenerator();
        TestEnergyFunction energy = new TestEnergyFunction();

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                stop, generator, energy, 1, new Random()
        );

        TestOrder result = sa.findSolution();

        assertNotNull(result, "SA must return a valid solution.");
        assertTrue(result.size() > 0, "Solution must contain at least one element.");
    }

    @Test
    void testConstructorWithNullArguments() {
        TestOrderGenerator generator = new TestOrderGenerator();
        TestEnergyFunction energy = new TestEnergyFunction();
        Random rand = new Random();

        assertThrows(NullPointerException.class,
                () -> new SimulatedAnnealing<>(null, generator, energy, 1, rand),
                "StoppingCondition cannot be null.");

        assertThrows(NullPointerException.class,
                () -> new SimulatedAnnealing<>(new TestStoppingCondition(10), null, energy, 1, rand),
                "EncodingGenerator cannot be null.");

        assertThrows(NullPointerException.class,
                () -> new SimulatedAnnealing<>(new TestStoppingCondition(10), generator, null, 1, rand),
                "FitnessFunction cannot be null.");

        assertThrows(NullPointerException.class,
                () -> new SimulatedAnnealing<>(new TestStoppingCondition(10), generator, energy, 1, null),
                "Random cannot be null.");
    }

    @Test
    void testAcceptWorseSolution() {
        TestStoppingCondition stop = new TestStoppingCondition(1);
        TestOrderGenerator generator = new TestOrderGenerator();
        TestEnergyFunction energy = new TestEnergyFunction();

        // Always accepts worse solutions
        Random forcedAccept = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }
        };

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                stop, generator, energy, 1, forcedAccept
        );

        TestOrder result = sa.findSolution();
        assertNotNull(result, "SA must still produce a solution.");
    }

    @Test
    void testNoImprovement() {
        TestStoppingCondition stop = new TestStoppingCondition(1);
        TestOrderGenerator generator = new TestOrderGenerator();
        TestEnergyFunction energy = new TestEnergyFunction();

        // Never accepts worse solutions
        Random forcedReject = new Random() {
            @Override
            public double nextDouble() {
                return 1.0;
            }
        };

        SimulatedAnnealing<TestOrder> sa = new SimulatedAnnealing<>(
                stop, generator, energy, 1, forcedReject
        );

        TestOrder result = sa.findSolution();
        assertNotNull(result, "Even without improvement, SA must return a valid solution.");
    }
}