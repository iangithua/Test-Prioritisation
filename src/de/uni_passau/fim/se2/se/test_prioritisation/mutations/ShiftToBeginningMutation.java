package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.Arrays;
import java.util.Random;

/**
 * A mutation that shifts a test to the beginning of the sequence.
 */
public class ShiftToBeginningMutation implements Mutation<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    public ShiftToBeginningMutation(final Random random) {
        this.random = random;
    }

    /**
     * Shifts a test to the beginning of the sequence.
     *
     * @param encoding the test order to be mutated
     * @return the mutated test order
     */
    @Override
    public TestOrder apply(TestOrder encoding) {

    int[] original = encoding.getPositions();
    int n = original.length;

    // Deep copy the array
    int[] mutated = Arrays.copyOf(original, n);

    // If only one test, mutation does nothing
    if (n <= 1) {
        return new TestOrder(encoding.getMutation(), mutated);
    }

    // Pick a random index not equal to 0
    int idx = random.nextInt(n - 1) + 1; // ensures 1..n-1
    int valueToMove = mutated[idx];

    // Shift everything right
    System.arraycopy(mutated, 0, mutated, 1, idx);

    // Put selected value at beginning
    mutated[0] = valueToMove;

    // Return new TestOrder
    return new TestOrder(encoding.getMutation(), mutated);
}
}
