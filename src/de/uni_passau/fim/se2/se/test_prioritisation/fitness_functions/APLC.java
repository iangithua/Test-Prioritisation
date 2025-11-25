package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;


/**
 * The Average Percentage of Lines Covered (APLC) fitness function.
 */
public final class APLC implements FitnessFunction<TestOrder> {

    /**
     * The coverage matrix to be used when computing the APLC metric.
     */
    private final boolean[][] coverageMatrix;

    /**
     * Creates a new APLC fitness function with the given coverage matrix.
     *
     * @param coverageMatrix the coverage matrix to be used when computing the APLC metric
     */
    public APLC(final boolean[][] coverageMatrix) {
                if (coverageMatrix == null) {
            throw new NullPointerException("Coverage matrix cannot be null");
        }
        this.coverageMatrix = coverageMatrix;
    }


    /**
     * Computes and returns the APLC for the given order of test cases.
     * Orderings that achieve a higher rate of coverage are rewarded with higher values.
     * The APLC ranges between 0.0 and 1.0.
     *
     * @param testOrder the proposed test order for which the fitness value will be computed
     * @return the APLC value of the given test order
     * @throws NullPointerException if {@code null} is given
     */
    @Override
  public double applyAsDouble(final TestOrder testOrder) {
    if (testOrder == null) {
        throw new NullPointerException("TestOrder cannot be null");
    }

    int numTests = testOrder.size();
    int numLines = coverageMatrix[0].length;

    // TL[lineIndex] = 1-based position of first test covering the line
    int[] firstCoverPosition = new int[numLines];

    int[] testOrderPositions = testOrder.getPositions();

    // Validate test indices
    for (int idx : testOrderPositions) {
        if (idx < 0 || idx >= coverageMatrix.length) {
            throw new IllegalArgumentException(
                "Test index " + idx + " is out of bounds for coverage matrix with " + coverageMatrix.length + " tests"
            );
        }
    }

    // Map test index -> position in order (1-based)
    int[] testIndexToPosition = new int[numTests];
    for (int orderPos = 0; orderPos < numTests; orderPos++) {
        testIndexToPosition[testOrderPositions[orderPos]] = orderPos + 1; // 1-based
    }

    // Update firstCoverPosition for each line
    for (int testIndex = 0; testIndex < numTests; testIndex++) {
        int position = testIndexToPosition[testIndex];
        boolean[] linesCoveredByTest = coverageMatrix[testIndex];

        for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
            if (linesCoveredByTest[lineIndex]) {
                if (firstCoverPosition[lineIndex] == 0 || position < firstCoverPosition[lineIndex]) {
                    firstCoverPosition[lineIndex] = position;
                }
            }
        }
    }

    // Compute APLC
    long sumPositions = 0;
    int coveredLines = 0;
    for (int pos : firstCoverPosition) {
        if (pos > 0) {
            sumPositions += pos;
            coveredLines++;
        }
    }

    if (coveredLines == 0) return 0.0;

    double n = numTests;
    double mPrime = coveredLines;
    double aplc = 1.0 - (sumPositions / (n * mPrime)) + (1.0 / (2.0 * n));

    // Clamp to [0,1]
    return Math.max(0.0, Math.min(1.0, aplc));
}


    /**
     * {@inheritDoc}
     */
    @Override
    public double maximise(TestOrder encoding) throws NullPointerException {
         return applyAsDouble(encoding);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double minimise(TestOrder encoding) throws NullPointerException {
        return 1.0 - applyAsDouble(encoding);


    }
}
