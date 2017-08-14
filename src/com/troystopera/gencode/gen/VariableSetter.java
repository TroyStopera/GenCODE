package com.troystopera.gencode.gen;

import com.troystopera.gencode.code.components.CodeBlock;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.VarType;

import java.util.Random;

/**
 * A class that contains functions to generate Statements that set variables' values.
 */
public class VariableSetter {

    //the minimum difficulty to be at before setting a variable's value to another variable
    private static final double MIN_DIF_FOR_VAR_ASSIGN = 0.33;

    private static final Random rand = new Random();

    static CodeBlock declareInts(double difficulty, String... vars) {
        CodeBlock block = new CodeBlock();
        if (vars.length == 0) return block;

        //assign the first variable a random value
        block.addExecutable(Declaration.declareWithAssign(vars[0], VarType.INT_PRIMITIVE, IntVar.random(100)));

        for (int i = 1; i < vars.length; i++) {
            //coin flip if difficulty appropriate for setting to var
            if (difficulty >= MIN_DIF_FOR_VAR_ASSIGN && rand.nextBoolean())
                block.addExecutable(Declaration.declareWithAssign(vars[i], VarType.INT_PRIMITIVE, vars[rand.nextInt(i)]));
            else block.addExecutable(Declaration.declareWithAssign(vars[i], VarType.INT_PRIMITIVE, IntVar.random(100)));
        }
        return block;
    }

}
