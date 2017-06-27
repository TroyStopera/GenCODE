package com.troystopera.gencode.gen;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.ProblemTopic;
import com.troystopera.gencode.ProblemType;

import java.util.Random;

/**
 * Simple abstract class for defining what functionality a Generator must provide.
 */
public abstract class Generator {

    protected static final int RANDOM_RETURN_MAX = 200;

    protected final Random random = new Random();
    private final VariableProvider variableProvider = new VariableProvider();
    private final ProblemTopic topic;

    protected Generator(ProblemTopic topic) {
        this.topic = topic;
    }

    public final ProblemTopic getTopic() {
        return topic;
    }

    public final Problem generate(double difficulty) {
        //no difficulty over 1 allowed, and must be > 0
        if (difficulty > 1) difficulty = 1;
        else if (difficulty <= 0) difficulty = 0.01;

        Problem.Builder builder = new Problem.Builder();

        ProblemType type = getRandomType(difficulty);
        builder.setType(type);
        builder.setTopic(getTopic());

        switch (type) {
            case RETURN_VALUE:
                return genReturnValue(difficulty, builder);
            case PRINT_OUT:
                return genPrintOut(difficulty, builder);
            case MISSING_CODE:
                return genWriteCode(difficulty, builder);
            default:
                throw new IllegalArgumentException("Unknown type: " + type.name());
        }
    }

    protected abstract Problem genReturnValue(double difficulty, Problem.Builder problemBuilder);

    protected abstract Problem genPrintOut(double difficulty, Problem.Builder problemBuilder);

    protected abstract Problem genWriteCode(double difficulty, Problem.Builder problemBuilder);

    final String newVarName() {
        return variableProvider.nextVar();
    }

    final String[] newVarNames(int count) {
        String[] arr = new String[count];
        for (int i = 0; i < arr.length; i++)
            arr[i] = newVarName();
        return arr;
    }

    /*
        Returns a random int between low (inclusive) and high (exclusive).
     */
    final int randomInt(int low, int high) {
        int add = low >= high ? 0 : random.nextInt(high - low);
        return low + add;
    }

    /*
        Returns a random number lass than max, weighted by the difficulty.
     */
    final int randomInt(int max, double difficulty) {
        double rand = -0.10;
        rand += (random.nextInt(21) / 100.0);

        //adjust the difficulty to allow for randomness
        difficulty += rand;

        //return a random int weighted by the difficulty
        return (int) Math.ceil(max * difficulty);
    }

    /*
        Returns a random boolean.
     */
    final boolean randomBool() {
        return random.nextBoolean();
    }

    /*
        Returns a random boolean that has the given probability.
     */
    final boolean randomBool(double probability) {
        //never allow a probability higher than 90% or less than 10%
        if (probability > .90) probability = .90;
        else if (probability < .10) probability = .10;
        return random.nextDouble() < probability;
    }

    private ProblemType getRandomType(double difficulty) {
        if (difficulty < 0.25)
            return ProblemType.RETURN_VALUE;
        else if (difficulty < 0.75)
            return random.nextBoolean() ? ProblemType.RETURN_VALUE : ProblemType.PRINT_OUT;
        else {
            ProblemType[] types = ProblemType.values();
            return types[random.nextInt(types.length)];
        }
    }

}
