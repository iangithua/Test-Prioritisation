package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;

import java.util.*;

public class TournamentSelection implements ParentSelection<TestOrder> {

    /**
     * A common default value for the size of the tournament.
     */
    private final static int DEFAULT_TOURNAMENT_SIZE = 5;
    private final int tournamentSize;
    private final APLC fitnessFunction;
    private final Random random;
    /**
     * Creates a new tournament selection operator.
     *
     * @param tournamentSize  the size of the tournament
     * @param fitnessFunction the fitness function used to rank the test orders
     * @throws NullPointerException if any of the arguments is {@code null}
     */
    public TournamentSelection(int tournamentSize, APLC fitnessFunction, Random random) {
        //Check on the passed values to be not null
        Objects.requireNonNull(fitnessFunction, "Fitness function must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        if (tournamentSize <= 0) 
            throw new IllegalArgumentException("Tournament size must be greater than zero");
        
            this.tournamentSize = tournamentSize;
            this.fitnessFunction = fitnessFunction;
            this.random = random;
    }

    /**
     * Creates a new tournament selection operator with a default tournament size.
     *
     * @param fitnessFunction the fitness function used to rank the test orders
     * @throws NullPointerException if any of the arguments is {@code null}
     */
    public TournamentSelection(APLC fitnessFunction, Random random) {
        this(DEFAULT_TOURNAMENT_SIZE, fitnessFunction, random);
    }

    /**
     * Selects a single parent from a population to be evolved in the current generation of an evolutionary algorithm
     * using the tournament selection strategy.
     *
     * @param population the population from which to select parents
     * @return the selected parent
     */
    @Override
    public TestOrder selectParent(List<TestOrder> population) {
        //Check if populationn is not null
        if(population == null || population.isEmpty())
            throw new IllegalArgumentException("Population must not be null or empty");

        TestOrder bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for(int i = 0; i < tournamentSize; i++) {
            // Randomly select an individual from the population
            TestOrder individual = population.get(random.nextInt(population.size()));
            double fitness = fitnessFunction.applyAsDouble(individual);

            // Update the best individual if the current one is better
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = individual;
            }
        }
        return bestIndividual;
    }
}
