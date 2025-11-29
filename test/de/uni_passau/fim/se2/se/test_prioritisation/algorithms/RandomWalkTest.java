package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.algorithms.RandomWalk;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RandomWalkTest {

    static class SimpleEncoding extends Encoding<SimpleEncoding> {
        private final int value;
        private final Mutation<SimpleEncoding> mutation;

        public SimpleEncoding(int value, Mutation<SimpleEncoding> mutation) {
            super(mutation);
            this.value = value;
            this.mutation = mutation;
        }

        public int getValue() {
            return value;
        }

        @Override
        public SimpleEncoding deepCopy() {
            return new SimpleEncoding(value, mutation);
        }

        @Override
        public SimpleEncoding self() {
            return this;
        }
    }


    static class SimpleMutation implements Mutation<SimpleEncoding> {
        @Override
        public SimpleEncoding apply(SimpleEncoding encoding) {
            return new SimpleEncoding((encoding.getValue() + 1) % 10, this);
        }
    }

  
    static class SimpleFitnessFunction implements FitnessFunction<SimpleEncoding> {
        @Override
        public double applyAsDouble(SimpleEncoding encoding) {
            return encoding.getValue();
        }

        @Override
        public double maximise(SimpleEncoding encoding) {
            return encoding.getValue();
        }

        @Override
        public double minimise(SimpleEncoding encoding) {
            return encoding.getValue();
        }
    }

   
    static class CountingStoppingCondition implements StoppingCondition {
        private final int maxEvaluations;
        private final AtomicInteger count = new AtomicInteger(0);

        public CountingStoppingCondition(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
        }

        @Override
        public void notifySearchStarted() {}

        @Override
        public void notifyFitnessEvaluation() {
            count.incrementAndGet();
        }

        @Override
        public boolean searchMustStop() {
            return count.get() >= maxEvaluations;
        }

        @Override
        public double getProgress() {
            return Math.min(1.0, (double) count.get() / maxEvaluations);
        }
    }

    @Test
    void randomWalkFindsBestValue() {
        SimpleMutation mutation = new SimpleMutation();
        EncodingGenerator<SimpleEncoding> generator = () -> new SimpleEncoding(0, mutation);
        SimpleFitnessFunction fitnessFunction = new SimpleFitnessFunction();
        CountingStoppingCondition stoppingCondition = new CountingStoppingCondition(20);

        RandomWalk<SimpleEncoding> randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);
        SimpleEncoding best = randomWalk.findSolution();

        // Best value should be 9 (max of SimpleEncoding modulo 10 mutation)
        assertEquals(9, best.getValue());
    }

    @Test
    void stoppingConditionRespected() {
        SimpleMutation mutation = new SimpleMutation();
        EncodingGenerator<SimpleEncoding> generator = () -> new SimpleEncoding(0, mutation);
        SimpleFitnessFunction fitnessFunction = new SimpleFitnessFunction();
        CountingStoppingCondition stoppingCondition = new CountingStoppingCondition(5);

        RandomWalk<SimpleEncoding> randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);
        randomWalk.findSolution();

        // Progress should be 100% after search
        assertEquals(1.0, stoppingCondition.getProgress(), 0.0001);
    }
}

