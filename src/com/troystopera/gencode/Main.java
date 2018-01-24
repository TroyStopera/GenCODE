package com.troystopera.gencode;

import com.troystopera.gencode.generator.CodeGenerator;
import com.troystopera.jkode.exec.Executor;
import com.troystopera.jkode.exec.Output;
import com.troystopera.jkode.format.JavaFormat;

import java.util.*;

/**
 * Created by troy on 8/1/17.
 */
public class Main {

    public static void main(String... args) {
        if (args.length < 4) {
            System.out.println("Please provide difficulty range, number of questions, and at least one topic");
            System.exit(-1);
        }

        double difficultyLow = Double.valueOf(args[0]);
        double difficultyHigh = Double.valueOf(args[1]);
        int count = Integer.valueOf(args[2]);
        ProblemTopic[] topics = new ProblemTopic[args.length - 3];

        for (int i = 3; i < args.length; i++)
            topics[i - 3] = ProblemTopic.valueOf(args[i]);

        System.out.println("Topics: " + Arrays.toString(topics));
        System.out.println("Difficulty: " + difficultyLow + " -> " + difficultyHigh);
        System.out.println("-------\n");

        CodeGenerator generator = new CodeGenerator(topics);
        int problemNum = 1;
        for (Problem problem : generator.generate(difficultyLow, difficultyHigh, count)) {
            System.out.println("Problem: " + problemNum);
            System.out.println(JavaFormat.INSTANCE.formatFunction(problem.getMainFunction(), ""));

            Executor exec = new Executor();
            Output output = exec.execute(problem.getMainFunction());
            System.out.println("Answer: " + output.getReturnVar().toString());
            System.out.println("\n\n");
            problemNum++;
        }
    }

}
