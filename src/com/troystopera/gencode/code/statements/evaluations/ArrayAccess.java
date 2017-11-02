package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/**
 * Evaluation of a value at an index in an array.
 */
public class ArrayAccess extends Evaluation<Var> {

    private final String array;
    private final int index;

    private ArrayAccess(String array, int index) {
        super(Type.ARRAY_ACCESS);
        this.array = array;
        this.index = index;
    }

    public String getArray() {
        return array;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        return Optional.of(scope.getArrVar(array, index));
    }

    public static ArrayAccess access(String array, int index) {
        return new ArrayAccess(array, index);
    }

}
