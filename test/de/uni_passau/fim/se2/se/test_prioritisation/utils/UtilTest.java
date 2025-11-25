package de.uni_passau.fim.se2.se.test_prioritisation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;


public class UtilTest {

@Test
public void testDegreesOfFreedom_normal() {
    assertEquals(4, Utils.degreesOfFreedom(5));
}

@Test
public void testDegreesOfFreedom_oneTest() {
    assertEquals(0, Utils.degreesOfFreedom(1));
}







}
