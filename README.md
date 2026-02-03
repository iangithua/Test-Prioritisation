# Test Case Prioritisation SCORE 96.6%

## Overview

This assignment implements meta-heuristic optimisation algorithms (random search, random walk, simulated annealing, and genetic algorithm) to solve the regression test case prioritisation problem.

## Problem Description

Regression testing verifies modified software, but executing entire test suites can be expensive. Test case prioritisation orders test cases to reveal faults as early as possible. This is an NP-hard scheduling problem where we seek the optimal ordering of test cases.

Given:
- A test suite **T** with test cases
- The set of permutations **Π(T)** of **T**
- A fitness function **f: Π(T) → ℝ**

Find: **T⋆ ∈ Π(T)** such that **∀T′ : T′ ∈ Π(T) : f(T′) ≥ f(T⋆)**

## Solution Encoding

Test suites are represented as integer arrays where values represent unique test cases.

**Constraints:**
- Number range: 0 to n-1
- Contiguous range
- No duplicate test cases

**Example:** `[2, 4, 1, 0, 3]` executes test 2 first, then test 4, etc.

### Genetic Operators

**Mutation (ShiftToBeginningMutation):** Shifts a random test case to the beginning of the sequence.
- Example: `[2, 4, 1, 0, 3]` → `[1, 2, 4, 0, 3]` (index 2 shifted)

**Crossover (OrderCrossover):** Two-point crossover copying a section from parent 1, then adding remaining elements in parent 2's order.
- Parents: `[1, 2, 3, 0, 4]` and `[3, 0, 2, 1, 4]`
- Child: `[2, 1, 3, 0, 4]` (indices 2-3 copied)

## Fitness Metric: APLC

Average Percentage of Line Coverage measures how quickly a test suite covers code lines.

**Formula:**
```
APLC(T′) = 1 - (1/nm) × (Σ TLᵢ) + 1/(2n)
```

Where:
- **n** = number of test cases
- **m** = number of coverable lines
- **TLᵢ** = position of first test covering line i (1-indexed)

Range: 0% to 100% (higher is better)

## Implementation Requirements

### Core Components

1. **TestOrder** - Solution encoding with validity checking
2. **TestOrderGenerator** - Random solution generator
3. **ShiftToBeginningMutation** - Mutation operator
4. **OrderCrossover** - Crossover operator for GA
5. **APLC** - Fitness function (adapt for 0-indexed arrays)
6. **TournamentSelection** - Parent selection for GA
7. **MaxFitnessEvaluations** - Stopping condition

### Algorithms

Implement four search algorithms that stop when the search budget (MaxFitnessEvaluations) is exhausted:
- Random Search
- Random Walk
- Simulated Annealing
- Genetic Algorithm

### Simulated Annealing Configuration

**Energy Function:** Use inverted APLC (since SA minimizes, APLC maximizes)

**Initial Temperature (τ₀):**
1. Perform n-iteration random walk from random initial configuration **c**
2. Compute average energy variation: `⟨ΔE⟩ := (1/n) × Σ|Eᵢ - Eᵢ₋₁|`
3. Choose initial acceptance probability **p₀**:
   - p₀ = 0.5 for average quality
   - p₀ = 0.2 for good quality
4. Compute: `τ₀ = -⟨ΔE⟩ / ln(p₀)` for ⟨ΔE⟩ ≥ 0
5. Use **c** as starting point

**Cooling Schedule:** Geometric law `τₖ₊₁ = 0.9 × τₖ`

**Equilibrium:** Reached after **Nₐccₑₚₜ = 12N** accepted mutations over at most **100N** tried moves (N = degrees of freedom)

## Usage

Build and run:
```bash
mvn package
java -jar target/Test_Prioritisation.jar -c Lift RS RW
```

**Test Classes:** AddNumbers, Lift, Rational, Complex

**Coverage Matrices:** Use `Utils.parseCoverageMatrix()` to parse boolean[][] arrays where `matrix[i][j]` indicates if test i covers line j.

## Development Notes

- Use `Randomness` class for random numbers (not `java.util.Random`)
- Test files must end with `Test` suffix
- Mutation analysis: `mvn clean test pitest:mutationCoverage`
- Pipeline timeout: 5 minutes

## Test Requirements

### Functional Tests (80% of points)
- ✅ TestOrder encoding (5/5)
- ✅ TestOrderGenerator (3/3)
- ✅ Degrees of freedom (2/2)
- ✅ ShiftToBeginningMutation (2/2)
- ✅ OrderCrossover (2/2)
- ✅ APLC edge cases (3/3)
- ✅ APLC general cases (3/3)
- ✅ APLC min/max functions (2/2)
- ✅ MaxFitnessEvaluations (3/3)
- ⚠️ TournamentSelection (2/3)
- ✅ Random Search (4/4)
- ✅ Random Walk (4/4)
- ⚠️ Simulated Annealing (3/4)
- ✅ Genetic Algorithm (4/4)

### Test Suite (20% of points)
- ✅ Line Coverage ≥ 80%
- ✅ Branch Coverage ≥ 80%
- ✅ Mutation Score ≥ 70%

## Deadlines

- **Release:** Nov 12, 2025 08:00
- **Due:** Dec 2, 2025 23:59
- **Complaints:** Not allowed
