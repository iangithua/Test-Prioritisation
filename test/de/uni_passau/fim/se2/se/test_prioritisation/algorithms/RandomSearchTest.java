package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomSearchTest {

    /**
     * Minimal concrete Encoding implementation
     */
    static class SimpleEncoding extends Encoding<SimpleEncoding> {
        private final double value;

        public SimpleEncoding(double value) {
            super(null); // no mutation needed for test
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public SimpleEncoding deepCopy() {
            return new SimpleEncoding(value);
        }

        @Override
        public SimpleEncoding self() {
            return this;
        }
    }

    /**
     * Simple fitness function: returns the value of the encoding
     */
    static class SimpleFitness implements FitnessFunction<SimpleEncoding> {
        @Override
        public double applyAsDouble(SimpleEncoding encoding) {
            return encoding.getValue();
        }

        @Override
        public double maximise(SimpleEncoding encoding) {
            return applyAsDouble(encoding);
        }

        @Override
        public double minimise(SimpleEncoding encoding) {
            return 1.0 - applyAsDouble(encoding);
        }
    }

    /**
     * Simple generator that cycles through predefined encodings
     */
    static class SimpleGenerator implements EncodingGenerator<SimpleEncoding> {
        private final SimpleEncoding[] encodings;
        private int index = 0;

        public SimpleGenerator(SimpleEncoding... encodings) {
            this.encodings = encodings;
        }

        @Override
        public SimpleEncoding get() {
            if (index >= encodings.length) index = 0;
            return encodings[index++];
        }
    }

    /**
     * Simple stopping condition that stops after N evaluations
     */
    static class SimpleStop implements StoppingCondition {
        private final int maxEvaluations;
        private int count = 0;

        public SimpleStop(int maxEvaluations) {
            this.maxEvaluations = maxEvaluations;
        }

        @Override
        public void notifySearchStarted() {
            count = 0;
        }

        @Override
        public void notifyFitnessEvaluation() {
            count++;
        }

        @Override
        public boolean searchMustStop() {
            return count >= maxEvaluations;
        }

        @Override
        public double getProgress() {
            return Math.min(1.0, (double) count / maxEvaluations);
        }
    }

    @Test
    void testRandomSearchFindsBestEncoding() {
        SimpleEncoding e1 = new SimpleEncoding(0.2);
        SimpleEncoding e2 = new SimpleEncoding(0.8);
        SimpleEncoding e3 = new SimpleEncoding(0.5);

        SimpleGenerator generator = new SimpleGenerator(e1, e2, e3);
        SimpleFitness fitness = new SimpleFitness();
        SimpleStop stop = new SimpleStop(3);

        RandomSearch<SimpleEncoding> search = new RandomSearch<>(stop, generator, fitness);

        SimpleEncoding best = search.findSolution();

        assertEquals(e2, best, "RandomSearch should return the encoding with the highest value");
        assertEquals(1.0, stop.getProgress(), 0.0001, "Progress should be 100% at the end");
    }
}

