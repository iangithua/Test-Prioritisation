package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

public class ShiftToBeginningMutationTest {

@Test
public void testShiftToBeginning() {
    Random random = new Random(0); // deterministic
    ShiftToBeginningMutation mut = new ShiftToBeginningMutation(random);
    int[] arr = {3, 0, 1, 2};
    TestOrder order = new TestOrder(mut, arr);

    TestOrder mutated = mut.apply(order);
    int[] result = mutated.getPositions();

    // With Random(0), idx chosen is consistent:
    // Should shift value at index 1 -> 0
    assertArrayEquals(new int[]{0, 3, 1, 2}, result);
}


}
