package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class OrderCrossoverTest {

    @Test
    void testCrossoverProducesValidChild() {
        Random rng = new Random(42);
        OrderCrossover crossover = new OrderCrossover(rng);

        int[] parent1Array = {0, 1, 2, 3, 4};
        int[] parent2Array = {4, 3, 2, 1, 0};

        TestOrder parent1 = new TestOrder(new ShiftToBeginningMutation(rng), parent1Array);
        TestOrder parent2 = new TestOrder(new ShiftToBeginningMutation(rng), parent2Array);

        TestOrder child = crossover.apply(parent1, parent2);

        // Child must contain all elements from 0..n-1
        int[] childPositions = child.getPositions();
        Arrays.sort(childPositions);
        for (int i = 0; i < childPositions.length; i++) {
            assertEquals(i, childPositions[i], "Child contains all elements 0..n-1");
        }
    }

    @Test
    void testCrossoverChildDifferentFromParents() {
        Random rng = new Random(123);
        OrderCrossover crossover = new OrderCrossover(rng);

        int[] parent1Array = {0, 1, 2, 3, 4, 5};
        int[] parent2Array = {5, 4, 3, 2, 1, 0};

        TestOrder parent1 = new TestOrder(new ShiftToBeginningMutation(rng), parent1Array);
        TestOrder parent2 = new TestOrder(new ShiftToBeginningMutation(rng), parent2Array);

        TestOrder child = crossover.apply(parent1, parent2);

        // Child should not be identical to both parents (usually)
        assertFalse(Arrays.equals(child.getPositions(), parent1.getPositions())
                && Arrays.equals(child.getPositions(), parent2.getPositions()),
                "Child should differ from both parents (in most runs)");
    }

    @Test
    void testCrossoverSingleElementParents() {
        Random rng = new Random(42);
        OrderCrossover crossover = new OrderCrossover(rng);

        int[] parent1Array = {0};
        int[] parent2Array = {0};

        TestOrder parent1 = new TestOrder(new ShiftToBeginningMutation(rng), parent1Array);
        TestOrder parent2 = new TestOrder(new ShiftToBeginningMutation(rng), parent2Array);

        TestOrder child = crossover.apply(parent1, parent2);

        assertArrayEquals(parent1Array, child.getPositions(), "Child should equal parents for single element");
    }

    @Test
    void testCrossoverPreservesMutationReference() {
        Random rng = new Random(42);
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(rng);
        OrderCrossover crossover = new OrderCrossover(rng);

        int[] parent1Array = {0, 1, 2, 3};
        int[] parent2Array = {3, 2, 1, 0};

        TestOrder parent1 = new TestOrder(mutation, parent1Array);
        TestOrder parent2 = new TestOrder(mutation, parent2Array);

        TestOrder child = crossover.apply(parent1, parent2);

        assertSame(parent1.getMutation(), child.getMutation(), "Child should preserve mutation reference from parent1");
    }
}
