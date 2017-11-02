package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;
import com.troystopera.gencode.var.VarType;

import java.util.Optional;

/**
 * Evaluation of a value literal.
 */
public class Value<T extends Var> extends Evaluation<T> {

    private final Var var;

    private Value(Var var) {
        super(Type.VALUE);
        this.var = var;
    }

    public Var getVar() {
        return var;
    }

    public static <T extends Var> Value<T> of(T var) {
        return new Value<>(var);
    }

    @Override
    protected Optional execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.of(var);
    }

}
