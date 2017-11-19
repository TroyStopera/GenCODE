package com.troystopera.gencode.code;

import com.troystopera.gencode.exec.Executable;
import com.troystopera.gencode.var.Var;

/***
 * Represents an Executable component of code, created from Statements and other Components.
 */
public abstract class Component extends Executable<Var> {

    private final Type type;

    protected Component(Type type) {
        this.type = type;
    }

    @Override
    public Executable.Type getExecType() {
        return Executable.Type.COMPONENT;
    }

    public final Type getType() {
        return type;
    }

    public enum Type {

        GENERIC, CONDITIONAL, FOR_LOOP, WHILE_LOOP, FUNCTION

    }

}
