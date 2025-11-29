package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import java.util.Random;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;


/**
 * Implements a random walk through the search space.
 *
 * @param <E> the type of encoding
 */
public final class RandomWalk<E extends Encoding<E>> implements SearchAlgorithm<E> {

    private  StoppingCondition stoppingCondition;
    private  EncodingGenerator<E> encodingGenerator;
    private  FitnessFunction<E> fitnessFunction;
    private  Random random;
    
    /**
     * Constructs a new random walk algorithm.
     *
     * @param stoppingCondition the stopping condition to use
     * @param encodingGenerator the encoding generator to use
     * @param fitnessFunction   the fitness function to use
     */
    public RandomWalk(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> fitnessFunction) {
        
        if (stoppingCondition == null) throw new NullPointerException("StoppingCondition cannot be null");
        if (encodingGenerator == null) throw new NullPointerException("EncodingGenerator cannot be null");
        if (fitnessFunction == null) throw new NullPointerException("FitnessFunction cannot be null");

        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Implements a random walk through the search space. First, a randomly chosen configuration is used as starting point.
     * Next, the search space is explored by taking a number of consecutive steps in some direction.
     * Finally, the best encountered configuration is chosen as the solution.
     *
     * @return the best solution found
     */
    @Override
    public E findSolution() {
   stoppingCondition.notifySearchStarted();

        // Start with a random encoding
        E currentEncoding = encodingGenerator.get();
        double currentFitness = fitnessFunction.applyAsDouble(currentEncoding);
        stoppingCondition.notifyFitnessEvaluation();

        // Keep track of the best solution found
        E bestEncoding = currentEncoding.deepCopy();
        double bestFitness = currentFitness;

        while (!stoppingCondition.searchMustStop()) {
            // Generate a random neighbor via mutation
            E neighbor = currentEncoding.getMutation().apply(currentEncoding);
            double neighborFitness = fitnessFunction.applyAsDouble(neighbor);
            stoppingCondition.notifyFitnessEvaluation();

            // Accept neighbor unconditionally (pure random walk)
            currentEncoding = neighbor;
            currentFitness = neighborFitness;

            // Update best if neighbor is better
            if (neighborFitness > bestFitness) {
                bestEncoding = neighbor.deepCopy();
                bestFitness = neighborFitness;
            }
        }

        return bestEncoding;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

}
