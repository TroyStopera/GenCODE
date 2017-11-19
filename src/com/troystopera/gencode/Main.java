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
        // if (args.length < 3)
        //     throw new IllegalArgumentException("Please provide difficulty, number of questions, and at least one topic");

        // double difficulty = Double.valueOf(args[0]);
        //  int count = Integer.valueOf(args[1]);
        // ProblemTopic[] topics = new ProblemTopic[args.length - 2];

        // for (int i = 2; i < args.length; i++)
        //    topics[i - 2] = ProblemTopic.valueOf(args[i]);

        CodeGenerator generator = new CodeGenerator(ProblemTopic.FOR_LOOP);
        Problem problem = generator.generate(0.25);

        System.out.println(Format.java().format(problem));

        ExecutorControl control = new ExecutorControl(problem);
        System.out.println(control.getOutput().getReturnVar().toString());
        System.out.println("--------");
        for (ExecOutput output : control.getFalseOutput(5)) {
            System.out.println(output.getReturnVar().toString());
        }
    }

}
