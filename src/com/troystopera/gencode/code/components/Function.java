package com.troystopera.gencode.code.components;

import com.troystopera.gencode.var.VarType;

/***
 * A callable function that may or may not return a value.
 */
public class Function extends CodeBlock {

    private final String name;
    private final VarType returnType;
    private final Argument[] args;

    public Function(String name, Argument... args) {
        this(name, null, args);
    }

    public Function(String name, VarType returnType, Argument... args) {
        super(Type.FUNCTION);
        this.name = name;
        this.returnType = returnType;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Argument[] getArgs() {
        return args;
    }

    public boolean hasReturnType() {
        return returnType != null;
    }

    public VarType getReturnType() {
        return returnType;
    }

    /***
     * Used to declare arguments for functions.
     */
    public static class Argument {

        public final VarType type;
        public final String name;

        public Argument(VarType type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
