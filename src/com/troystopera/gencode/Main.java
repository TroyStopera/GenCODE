package com.troystopera.gencode;

import com.troystopera.gencode.exec.ExecOutput;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.format.Format;
import com.troystopera.gencode.gen.ConditionalGenerator;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.Var;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by troy on 8/1/17.
 */
public class Main {

    public static void main(String... args) {
        ConditionalGenerator generator = new ConditionalGenerator();
        Problem problem = generator.generate(50);
        ExecutorControl control = new ExecutorControl(problem);
        Var correctVar = control.getOutput().getReturnVar();

        HashSet<Var> choiceSet = new HashSet<>();
        choiceSet.add(correctVar);
        for (ExecOutput output : control.getFalseOutput(4))
            choiceSet.add(output.getReturnVar());
        while (choiceSet.size() <= 4) choiceSet.add(IntVar.random(200));

        List<Var> choices = new LinkedList<>(choiceSet);
        Collections.shuffle(choices);

        StringBuilder builder = new StringBuilder();
        builder.append(Format.java().format(problem));
        builder.append("\nWhat would this function return?");
        char optLetter = 'a';
        for (Var var : choices) {
            if (var == null) continue;
            builder.append("\n    ");
            builder.append(optLetter++);
            builder.append(") ");
            builder.append(var.toString());
            if (var.equals(correctVar)) builder.append(" <-------");
        }

        System.out.println(builder.toString());
    }

}
