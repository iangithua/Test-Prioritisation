package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class APLCTest {

    @Test
    public void testPerfectCoverageOrder() {
        // 3 tests, 4 lines
        // Each test covers one line, sequentially
        boolean[][] coverageMatrix = {
                { true, false, false, false }, // Test 0 covers line 0
                { false, true, false, false }, // Test 1 covers line 1
                { false, false, true, true } // Test 2 covers line 2 and 3
        };

        Mutation<TestOrder> noopMutation = encoding -> encoding; // returns the same encoding
        int[] order = { 0, 1, 2 };
        TestOrder perfectOrder = new TestOrder(noopMutation, order);
        APLC aplc = new APLC(coverageMatrix);

        double fitness = aplc.applyAsDouble(perfectOrder);

        assertTrue(fitness > 0.0 && fitness <= 1.0, "Fitness should be in [0,1]");
    }

    @Test
    public void testWorstCoverageOrder() {
        // 3 tests, 4 lines
        boolean[][] coverageMatrix = {
                { true, false, false, false },
                { false, true, false, false },
                { false, false, true, true }
        };

        // Reverse order (worst-case scenario)
        Mutation<TestOrder> noopMutation = encoding -> encoding; // returns the same encoding
        int[] order = { 0, 1, 2 };
        TestOrder worstOrder = new TestOrder(noopMutation, order);
        APLC aplc = new APLC(coverageMatrix);

        double fitness = aplc.applyAsDouble(worstOrder);

        assertTrue(fitness > 0.0 && fitness <= 1.0, "Fitness should still be in [0,1]");
        assertTrue(fitness < 1.0, "Worst order fitness should be less than perfect coverage");
    }

    @Test
    public void testPartialCoverageOrder() {
        // 4 tests, 4 lines
        boolean[][] coverageMatrix = {
                { true, false, false, false },
                { true, true, false, false },
                { false, false, true, false },
                { false, false, true, true }
        };

         Mutation<TestOrder> noopMutation = encoding -> encoding; // returns the same encoding
        int[] order = { 0, 1, 2 };
        TestOrder partialOrder = new TestOrder(noopMutation, order);
        APLC aplc = new APLC(coverageMatrix);

        double fitness = aplc.applyAsDouble(partialOrder);

        assertTrue(fitness > 0.0 && fitness <= 1.0, "Fitness should be in [0,1]");
    }

    @Test
    public void testNoCoverage() {
        // 3 tests, 3 lines, but no test covers any line
        boolean[][] coverageMatrix = {
                { false, false, false },
                { false, false, false },
                { false, false, false }
        };

         Mutation<TestOrder> noopMutation = encoding -> encoding; // returns the same encoding
        int[] order = { 0, 1, 2 };
        TestOrder testOrder = new TestOrder(noopMutation, order);
        APLC aplc = new APLC(coverageMatrix);

        double fitness = aplc.applyAsDouble(testOrder);

        assertEquals(0.0, fitness, "If no lines are covered, fitness should be 0");
    }

    @Test
    public void testMaximiseMinimiseConsistency() {
        boolean[][] coverageMatrix = {
                { true, false },
                { false, true }
        };

        Mutation<TestOrder> noopMutation = encoding -> encoding; // returns the same encoding
        int[] order = { 0, 1 };
        TestOrder testOrder = new TestOrder(noopMutation, order);
        APLC aplc = new APLC(coverageMatrix);

        double maxValue = aplc.maximise(testOrder);
        double minValue = aplc.minimise(testOrder);

        assertEquals(maxValue, aplc.applyAsDouble(testOrder), "Maximise should equal applyAsDouble");
        assertEquals(1.0 - maxValue, minValue, "Minimise should be 1 - APLC value");
    }
}
