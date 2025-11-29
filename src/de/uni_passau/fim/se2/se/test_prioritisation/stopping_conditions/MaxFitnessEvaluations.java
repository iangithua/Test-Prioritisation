package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * Stopping condition that stops the search after a specified number of fitness evaluations.
 */
public class MaxFitnessEvaluations implements StoppingCondition {

    private final int maxFitnessEvaluations;

     //Counter for how many evaluations have been performed so far.
    private int currentEvaluations;

    public MaxFitnessEvaluations(final int maxFitnessEvaluations) {
        this.maxFitnessEvaluations = maxFitnessEvaluations;
    }

    @Override
    public void notifySearchStarted() {
        // Reset the counter when search begins
        this.currentEvaluations = 0;
    }

    @Override
    public void notifyFitnessEvaluation() {
        // Increment the counter every time fitness is evaluated
        this.currentEvaluations++;
    }

    @Override
    public boolean searchMustStop() {
        // Stop when the limit is reached
        return this.currentEvaluations >= this.maxFitnessEvaluations;
    }

    @Override
    public double getProgress() {
        // A value between 0.0 and 1.0 that indicates progress toward the stopping limit
        if (maxFitnessEvaluations == 0) {
            return 1.0;
        }
        return Math.min(1.0, (double) this.currentEvaluations / this.maxFitnessEvaluations);
    }
}
