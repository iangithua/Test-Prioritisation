package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

public class TestOrderTest {

@Test
public void testGet_generatesValidPermutation() {
    Random random = new Random(123);
    Mutation<TestOrder> dummyMutation = x -> {
        return x;};
    int testCases = 5;

    TestOrderGenerator generator = new TestOrderGenerator(random, dummyMutation, testCases);
    TestOrder order = generator.get();

    assertEquals(testCases, order.size());
    assertTrue(TestOrder.isValid(order.getPositions()));
}

@Test
public void testGet_randomness() {
    Random random = new Random();
    Mutation<TestOrder> dummyMutation = x -> {
        return x;};

    TestOrderGenerator gen = new TestOrderGenerator(random, dummyMutation, 5);

    TestOrder o1 = gen.get();
    TestOrder o2 = gen.get();

    assertFalse(Arrays.equals(o1.getPositions(), o2.getPositions()));
}

@Test
public void testDeepCopy() {
    Mutation<TestOrder> dummyMutation = x -> {
        return x;}; // no-op mutation
    int[] positions = {0, 1, 2, 3};

    TestOrder original = new TestOrder(dummyMutation, positions);
    TestOrder copy = original.deepCopy();

    assertNotSame(original, copy);
    assertNotSame(original.getPositions(), copy.getPositions());

    assertArrayEquals(original.getPositions(), copy.getPositions());
}

@Test
public void testIsValid_numberTooLarge() {
    int[] arr = {0, 1, 5}; // > length-1
    assertFalse(TestOrder.isValid(arr));
}

@Test
public void testIsValid_negativeNumber() {
    int[] arr = {-1, 0, 1};
    assertFalse(TestOrder.isValid(arr));
}

@Test
public void testIsValid_missingNumber() {
    int[] arr = {0, 1, 3, 4}; // missing 2, out of range
    assertFalse(TestOrder.isValid(arr));
}

@Test
public void testIsValid_duplicateNumbers() {
    int[] arr = {1, 2, 2, 0};
    assertFalse(TestOrder.isValid(arr));
}

@Test
public void testIsValid_validPermutation() {
    int[] arr = {2, 0, 1, 3};
    assertTrue(TestOrder.isValid(arr));
}




}
