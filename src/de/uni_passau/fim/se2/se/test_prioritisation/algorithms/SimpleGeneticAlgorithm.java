package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class SimpleGeneticAlgorithm<E extends Encoding<E>> implements SearchAlgorithm<E> {

    /**
     * Creates a new simple genetic algorithm with the given components.
     *
     * @param stoppingCondition the stopping condition to be used by the genetic algorithm
     * @param encodingGenerator the encoding generator used to create the initial population
     * @param fitnessFunction   the fitness function used to evaluate the quality of the individuals in the population
     * @param crossover         the crossover operator used to create offspring from parents
     * @param parentSelection   the parent selection operator used to select parents for the next generation
     * @param random            the source of randomness for this algorithm
     */

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> fitnessFunction;
    private final Crossover<E> crossover;
    private final ParentSelection<E> parentSelection;
    private final Random random;

    private List<E> population;
    private E bestSolution;
    private double bestFitness;

    public static final int POPULATION_SIZE = 100; // Adjusted population size
    private static double CROSSOVER_PROBABILITY = 0.9;

    public SimpleGeneticAlgorithm(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> fitnessFunction,
            final Crossover<E> crossover,
            final ParentSelection<E> parentSelection,
            final Random random) {
        if (stoppingCondition == null || encodingGenerator == null || fitnessFunction == null
                || crossover == null || parentSelection == null || random == null) {
            throw new NullPointerException("Arguments cannot be null.");
        }

        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.fitnessFunction = fitnessFunction;
        this.crossover = crossover;
        this.parentSelection = parentSelection;
        this.random = random;
        this.population = new ArrayList<>();
        this.bestSolution = null;
        this.bestFitness = Double.NEGATIVE_INFINITY;
    }

    /**
     * Runs the genetic algorithm to find a solution to the given problem.
     *
     * @return the best individual found by the genetic algorithm
     */
    @Override
    public E findSolution() {
        stoppingCondition.notifySearchStarted();

        createInitialPopulation();
        scorePopulation();

        while (!stoppingCondition.searchMustStop()) {

            // Carry over the elite individual
            List<E> nextGeneration = preserveElite();

            // Fill remaining population slots
            while (nextGeneration.size() < POPULATION_SIZE) {

                E p1 = parentSelection.selectParent(population);
                E p2 = parentSelection.selectParent(population);

                E child;

                // Decide whether to apply crossover
                if (random.nextDouble() <= CROSSOVER_PROBABILITY) {
                    child = crossover.apply(p1, p2);
                } else {
                    // Take a copy of one of the parents instead
                    child = (random.nextBoolean() ? p1 : p2).deepCopy();
                }

                nextGeneration.add(child);
            }

            // Mutation phase: always mutate every individual
            List<E> mutatedGeneration = new ArrayList<>(POPULATION_SIZE);
            for (E entity : nextGeneration) {
                mutatedGeneration.add(entity.mutate());
            }

            // Replace old generation
            population = mutatedGeneration;

            // Re-evaluate all individuals in the new generation
            scorePopulation();
        }

        return bestSolution;
    }

    public void createInitialPopulation() {
        population = new ArrayList<>(POPULATION_SIZE);
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(encodingGenerator.get());
        }
    }

    public void scorePopulation() {
        for (E entity : population) {
            double score = fitnessFunction.applyAsDouble(entity);
            stoppingCondition.notifyFitnessEvaluation();

            if (score > bestFitness) {
                bestFitness = score;
                bestSolution = entity.deepCopy();
            }
        }
    }

    public List<E> preserveElite() {
        List<E> eliteGroup = new ArrayList<>();
        if (bestSolution != null) {
            eliteGroup.add(bestSolution.deepCopy());
        }
        return eliteGroup;
    }

    public void setPopulation(List<E> newPop) {
        if (newPop == null || newPop.size() != POPULATION_SIZE) {
            throw new IllegalArgumentException("Population must be non-null and exactly " + POPULATION_SIZE);
        }
        population = new ArrayList<>(newPop);
    }

    public void setCrossoverChance(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be between 0 and 1.");
        }
        this.CROSSOVER_PROBABILITY = probability;
    }

    public List<E> getPopulation() {
        return new ArrayList<>(population);
    }


    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
