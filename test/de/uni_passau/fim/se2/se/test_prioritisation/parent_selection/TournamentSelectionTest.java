package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TournamentSelectionTest {

    /** Simple mutation stub for TestOrder */
    private static class NoOpMutation implements Mutation<TestOrder> {
        @Override
        public TestOrder apply(TestOrder encoding) { return encoding; }
    }

    /** Helper to create TestOrder */
    private TestOrder makeOrder(int... positions) {
        return new TestOrder(new NoOpMutation(), positions);
    }

    @Test
    void testConstructorRejectsNullFitnessFunction() {
        assertThrows(NullPointerException.class,
            () -> new TournamentSelection(3, null, new Random()));
    }

    @Test
    void testConstructorRejectsNullRandom() {
        boolean[][] matrix = { {true} };
        APLC aplc = new APLC(matrix);

        assertThrows(NullPointerException.class,
            () -> new TournamentSelection(3, aplc, null));
    }

    @Test
    void testConstructorRejectsInvalidTournamentSize() {
        boolean[][] matrix = { {true} };
        APLC aplc = new APLC(matrix);

        assertThrows(IllegalArgumentException.class,
            () -> new TournamentSelection(0, aplc, new Random()));
    }

    @Test
    void testSelectParentRejectsNullPopulation() {
        boolean[][] matrix = { {true} };
        APLC aplc = new APLC(matrix);

        TournamentSelection selection = new TournamentSelection(aplc, new Random());

        assertThrows(IllegalArgumentException.class,
            () -> selection.selectParent(null));
    }

    @Test
    void testSelectParentRejectsEmptyPopulation() {
        boolean[][] matrix = { {true} };
        APLC aplc = new APLC(matrix);

        TournamentSelection selection = new TournamentSelection(aplc, new Random());

        assertThrows(IllegalArgumentException.class,
            () -> selection.selectParent(new ArrayList<>()));
    }

    @Test
    void testSelectsFittestIndividualInSmallTournament() {
        boolean[][] coverageMatrix = {
                {true, false},  // test 0
                {true, true}    // test 1 covers more lines
        };

        APLC aplc = new APLC(coverageMatrix);

        TestOrder weak = makeOrder(0, 1); // lower APLC
        TestOrder strong = makeOrder(1, 0); // higher APLC

        List<TestOrder> population = List.of(weak, strong);

        Random deterministic = new Random(1);

        TournamentSelection selector =
                new TournamentSelection(2, aplc, deterministic);

        TestOrder selected = selector.selectParent(population);

        assertEquals(strong, selected,
                "Tournament should select the individual with higher fitness");
    }

}

