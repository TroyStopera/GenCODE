package com.troystopera.gencode.code.components;

import com.troystopera.gencode.val.ValType;

/***
 * A callable function that may or may not return a value.
 */
public class Function extends CodeBlock {

    private final String name;
    private final ValType returnType;
    private final Argument[] args;

    public Function(String name, Argument... args) {
        this(name, null, args);
    }

    public Function(String name, ValType returnType, Argument... args) {
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

    public ValType getReturnType() {
        return returnType;
    }

    /***
     * Used to declare arguments for functions.
     */
    public static class Argument {

        public final ValType type;
        public final String name;

        public Argument(ValType type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
