package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/**
 * Evaluating a variable value.
 */
public class Variable extends Evaluation {

    private final String varName;

    private Variable(String name) {
        super(Type.VARIABLE);
        varName = name;
    }

    public String getName() {
        return varName;
    }

    public static Variable of(String varName) {
        return new Variable(varName);
    }

    @Override
    protected Optional execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.of(scope.getVal(varName));
    }

}
