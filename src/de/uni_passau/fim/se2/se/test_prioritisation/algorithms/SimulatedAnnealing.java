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
    public E currentSolution;
    public E bestSolution;
    public double bestEnergy;  // Track the best observed energy value

    public double temperature;
    private final double coolingRate;  // Rate at which temperature decreases
    private final double initialAcceptanceProbability;  // Initial acceptance probability for worse solutions

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

        // Set initial temperature and cooling rate
        this.temperature = 1.0; // Initial temperature
        this.coolingRate = 0.99;  // Rate at which the temperature decreases, typically between 0.8-1.0
        this.initialAcceptanceProbability = 0.5;  // Initial probability to accept worse solutions
    }

    /**
     * Performs the Simulated Annealing algorithm to search for an optimal solution of the encoded problem.
     * Since Simulated Annealing is designed as a minimisation algorithm, optimal solutions are characterized by a minimal energy value.
     */
    @Override
    public E findSolution() {
        stoppingCondition.notifySearchStarted();

        // Initial solution
        currentSolution = encodingGenerator.get();
        double currentEnergy = energy.applyAsDouble(currentSolution);

        bestSolution = currentSolution;
        double bestEnergy = currentEnergy;

        double temperature = INITIAL_TEMPERATURE;

        while (!stoppingCondition.searchMustStop()) {
// Generate a neighboring solution (mutation)
            E neighbor = currentSolution.mutate();

            // Evaluate the energy of the neighboring solution
            double neighborEnergy = energy.minimise(neighbor);

            // Calculate the energy difference (ΔE)
            double deltaE = neighborEnergy - bestEnergy;

            // If the neighbor has lower energy, or if a worse solution is accepted based on temperature
            if (deltaE < 0 || random.nextDouble() < Math.exp(-deltaE / temperature)) {
                currentSolution = neighbor;
                if (neighborEnergy <= bestEnergy) {  // Minimize energy
                    bestSolution = neighbor;
                    bestEnergy = neighborEnergy;
                }
            }

            // Reduce the temperature according to the cooling rate
            temperature *= coolingRate;

            // Notify the stopping condition about the fitness evaluation
            stoppingCondition.notifyFitnessEvaluation();
        }

        // Return the best solution found
        return bestSolution;
    }

    @Override
    public StoppingCondition getStoppingCondition() {

        return stoppingCondition;
    }
}
