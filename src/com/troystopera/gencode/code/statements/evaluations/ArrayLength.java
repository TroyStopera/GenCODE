package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.ArrayVar;
import com.troystopera.gencode.var.IntVar;

import java.util.Optional;

/**
 * Evaluation of the length of an array.
 */
public class ArrayLength extends Evaluation<IntVar> {

    private final String array;

    private ArrayLength(String array) {
        super(Type.ARRAY_LENGTH);
        this.array = array;
    }

    public String getArray() {
        return array;
    }

    @Override
    protected Optional<IntVar> execute(ExecutorControl control, Console console, Scope scope) {
        ArrayVar array = (ArrayVar) scope.getVar(this.array);
        return Optional.of(IntVar.of(array.length()));
    }

    public static ArrayLength of(String array) {
        return new ArrayLength(array);
    }

}
