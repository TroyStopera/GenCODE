package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/**
 * Evaluating a variable value.
 */
public class Variable<T extends Var> extends Evaluation<T> {

    private final String varName;

    private Variable(String name) {
        super(Type.VARIABLE);
        varName = name;
    }

    public String getName() {
        return varName;
    }

    public static <T extends Var> Variable<T> of(String varName) {
        return new Variable<>(varName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Optional<T> execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.of((T) scope.getVar(varName));
    }

}
