package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Objects;
import java.util.Random;

/**
 * A generator for random test case orderings of a regression test suite. In the literature, indices
 * would start at 1. However, we let them start at 0 as this simplifies the implementation. The
 * highest index is given by the number of test cases minus 1. The range of indices is contiguous.
 */
public class TestOrderGenerator implements EncodingGenerator<TestOrder> {

    /**
     * Creates a new test order generator with the given mutation and number of test cases.
     *
     * @param random     the source of randomness
     * @param mutation   the elementary transformation that the generated orderings will use
     * @param testCases  the number of test cases in the ordering
     */

    private final Random random;
    private final Mutation<TestOrder> mutation;
    private final int testCases;


    public TestOrderGenerator(final Random random, final Mutation<TestOrder> mutation, final int testCases) {
        this.random = Objects.requireNonNull(random);
        this.mutation = Objects.requireNonNull(mutation);
        this.testCases = testCases;
    }

    /**
     * Creates and returns a random permutation of test cases.
     *
     * @return random test case ordering
     */
    @Override
    public TestOrder get() {
        // Build array [0, 1, 2, ..., testCases - 1]
        int[] positions = new int[testCases];
        for (int i = 0; i < testCases; i++) {
            positions[i] = i;
        }

        // Fisher–Yates shuffle
        for (int i = testCases - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = positions[i];
            positions[i] = positions[j];
            positions[j] = temp;
        }

        // Create a new TestOrder using mutation and shuffled array
        return new TestOrder(mutation, positions);
    }
}
