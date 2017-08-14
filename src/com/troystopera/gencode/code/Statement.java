package com.troystopera.gencode.code;

import com.troystopera.gencode.exec.Executable;
import com.troystopera.gencode.var.Var;

/***
 * Represents an executable statement in generated code. Statements are executed once the code generates to
 * determine the results of the code.
 */
public abstract class Statement<T extends Var> extends Executable<T> {

    private final Type type;

    public Statement(Type type) {
        this.type = type;
    }

    @Override
    public Executable.Type getExecType() {
        return Executable.Type.STATEMENT;
    }

    public final Type getType() {
        return type;
    }

    public enum Type {
        //TODO: Make PRINT statement
        ASSIGNMENT, DECLARATION, RETURN, EVALUATION
    }

}
