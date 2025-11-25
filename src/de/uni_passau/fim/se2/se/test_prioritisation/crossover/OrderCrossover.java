package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.*;

public class OrderCrossover implements Crossover<TestOrder> {

     /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Creates a new order crossover operator.
     *
     * @param random the internal source of randomness
     */
    public OrderCrossover(final Random random) {
        this.random = random;
    }

    /**
     * Combines two parent encodings to create a new offspring encoding using the order crossover operation.
     * The order crossover corresponds to a two-point crossover where the section between two random indices is copied
     * from the first parent and the remaining alleles are added in the order they appear in the second parent.
     * The resulting children must correspond to a valid test order encoding of size n that represents a permutation of tests
     * where each test value in the range [0, n-1] appears exactly once.
     *
     * @param parent1 the first parent encoding
     * @param parent2 the second parent encoding
     * @return the offspring encoding
     */
    @Override
    public TestOrder apply(TestOrder parent1, TestOrder parent2) {
        int numTests = parent1.size();
    int[] parent1Order = parent1.getPositions();
    int[] parent2Order = parent2.getPositions();

    int[] childOrder = new int[numTests];
    Arrays.fill(childOrder, -1); // initialize with empty slots

    Random rng = new Random(); // or pass a Random instance to the constructor

    // Randomly select two crossover points
    int index1 = rng.nextInt(numTests);
    int index2 = rng.nextInt(numTests);
    int crossoverStart = Math.min(index1, index2);
    int crossoverEnd = Math.max(index1, index2);

    // Step 1: Copy the segment from parent1 into the child
    for (int i = crossoverStart; i <= crossoverEnd; i++) {
        childOrder[i] = parent1Order[i];
    }

    // Step 2: Fill remaining positions from parent2 in order, skipping duplicates
    int fillIndex = (crossoverEnd + 1) % numTests; // start after copied segment
    for (int i = 0; i < numTests; i++) {
        int candidateTest = parent2Order[(crossoverEnd + 1 + i) % numTests];

        // Skip candidate if it is already in the copied segment
        boolean alreadyInChild = false;
        for (int j = crossoverStart; j <= crossoverEnd; j++) {
            if (childOrder[j] == candidateTest) {
                alreadyInChild = true;
                break;
            }
        }

        if (!alreadyInChild) {
            childOrder[fillIndex] = candidateTest;
            fillIndex = (fillIndex + 1) % numTests;
            if (fillIndex == crossoverStart) break; // all positions filled
        }
    }

    // Return new TestOrder with the same mutation as parent1
    return new TestOrder(parent1.getMutation(), childOrder);
    }
}
