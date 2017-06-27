package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/**
 * Evaluation of a value literal..
 */
public class Value extends Evaluation {

    private final Val val;

    private Value(Val val) {
        super(Type.VALUE);
        this.val = val;
    }

    public Val getVal() {
        return val;
    }

    public static Value of(Val val) {
        return new Value(val);
    }

    @Override
    protected Optional execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.of(val);
    }

}
