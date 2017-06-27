package com.troystopera.gencode.code;

import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.Executable;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/**
 * Represents a blank line used to make generated code more readable.
 */
public class BlankLine extends Executable {

    private static final BlankLine instance = new BlankLine();

    private BlankLine() {
        //no direct instantiation
    }

    @Override
    protected Optional execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.empty();
    }

    @Override
    public Type getExecType() {
        return Type.EMPTY;
    }

    public static BlankLine get() {
        return instance;
    }

}
