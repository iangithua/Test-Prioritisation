package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.Objects;
import java.util.Random;


/**
 * Implements the Simulated Annealing algorithm for test order prioritisation based on
 * -----------------------------------------------------------------------------------------
 * Flow chart of the algorithm:
 * Bastien Chopard, Marco Tomassini, "An Introduction to Metaheuristics for Optimization",
 * (Springer), Ch. 4.3, Page 63
 * -----------------------------------------------------------------------------------------
 * Note we've applied a few modifications to add elitism.
 *
 * @param <E> the type of encoding
 */
public final class SimulatedAnnealing<E extends Encoding<E>> implements SearchAlgorithm<E> {

    /**
     * Constructs a new simulated annealing algorithm.
     *
     * @param stoppingCondition the stopping condition to use
     * @param encodingGenerator the encoding generator to use
     * @param energy            the energy fitness function to use
     * @param degreesOfFreedom  the number of degrees of freedom of the problem, i.e. the number of variables that define a solution
     * @param random            the random number generator to use
     */

    private static final double INITIAL_TEMPERATURE = 1.0;
    private static final double COOLING_RATE = 0.995;

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> energy;
    private final int degreesOfFreedom;
    private final Random random;

    public SimulatedAnnealing(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> energy,
            final int degreesOfFreedom,
            final Random random) {
        this.stoppingCondition = Objects.requireNonNull(stoppingCondition);
        this.encodingGenerator = Objects.requireNonNull(encodingGenerator);
        this.energy = Objects.requireNonNull(energy);
        this.degreesOfFreedom = degreesOfFreedom;
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Performs the Simulated Annealing algorithm to search for an optimal solution of the encoded problem.
     * Since Simulated Annealing is designed as a minimisation algorithm, optimal solutions are characterized by a minimal energy value.
     */
    @Override
    public E findSolution() {
        stoppingCondition.notifySearchStarted();

        // Initial solution
        E currentSolution = encodingGenerator.get();
        double currentEnergy = energy.applyAsDouble(currentSolution);

        E bestSolution = currentSolution;
        double bestEnergy = currentEnergy;

        double temperature = INITIAL_TEMPERATURE;

        while (!stoppingCondition.searchMustStop()) {

            // Propose a neighbour
            E neighbour = currentSolution.mutate();
            stoppingCondition.notifyFitnessEvaluation();
            double neighbourEnergy = energy.applyAsDouble(neighbour);

            // Accept if better OR with Metropolis probability
            boolean accept =
                    neighbourEnergy < currentEnergy ||
                            random.nextDouble() < Math.exp((currentEnergy - neighbourEnergy) / temperature);

            if (accept) {
                currentSolution = neighbour;
                currentEnergy = neighbourEnergy;
            }

            // Elitism: track global best
            if (currentEnergy < bestEnergy) {
                bestSolution = currentSolution;
                bestEnergy = currentEnergy;
            }

            // Cool down
            temperature = temperature * COOLING_RATE;
        }

        return bestSolution;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        throw new UnsupportedOperationException("Implement me");
    }
}
