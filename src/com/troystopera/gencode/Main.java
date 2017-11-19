package com.troystopera.gencode;

import com.troystopera.gencode.exec.ExecOutput;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.format.Format;
import com.troystopera.gencode.generator.CodeGenerator;
import com.troystopera.gencode.var.Var;

import java.util.*;

/**
 * Created by troy on 8/1/17.
 */
public class Main {

    public static void main(String... args) {
        if (args.length < 3) {
            System.out.println("Please provide difficulty, number of questions, and at least one topic");
            System.exit(-1);
        }

        double difficulty = Double.valueOf(args[0]);
        int count = Integer.valueOf(args[1]);
        ProblemTopic[] topics = new ProblemTopic[args.length - 2];

        for (int i = 2; i < args.length; i++)
            topics[i - 2] = ProblemTopic.valueOf(args[i]);

        System.out.println("Topics: " + Arrays.toString(topics));
        System.out.println("Difficulty: " + difficulty);
        System.out.println("-------\n");

        CodeGenerator generator = new CodeGenerator(topics);
        int problemNum = 1;
        for (Problem problem : generator.generate(difficulty, count)) {
            System.out.println("Problem: " + problemNum);
            System.out.println(Format.java().format(problem));

            ExecutorControl control = new ExecutorControl(problem);
            System.out.println("Answer: " + control.getOutput().getReturnVar().toString());
            System.out.print("Distractors: ");
            for (ExecOutput output : control.getFalseOutput(3)) {
                System.out.print(output.getReturnVar().toString() + "  ");
            }
            System.out.println("\n\n");
            problemNum++;
        }
    }

}
