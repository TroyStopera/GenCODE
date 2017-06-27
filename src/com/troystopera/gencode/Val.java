package com.troystopera.gencode;

import java.util.Random;

/***
 * High-level representation of a value.
 * Allows for the generated code to use any data type in operations and variable declarations.
 */
public abstract class Val<T> {

    private final T val;

    private Val(T val) {
        this.val = val;
    }

    public abstract ValType type();

    public final T get() {
        return val;
    }

    public boolean asBoolean() {
        if (val instanceof java.lang.Boolean) return (java.lang.Boolean) val;
        else return java.lang.Boolean.valueOf(val.toString());
    }

    public java.lang.String asString() {
        if (val instanceof java.lang.String) return (java.lang.String) val;
        else return val.toString();
    }

    public int asInt() {
        if (val instanceof Integer) return (Integer) val;
        else return Integer.valueOf(val.toString());
    }

    public boolean equals(Val v) {
        //works for all data types
        return this.toString().equals(v.toString());
    }

    @Override
    public java.lang.String toString() {
        return val.toString();
    }

    public static Boolean booleanVal(boolean val) {
        return new Boolean(val);
    }

    public static String stringVal(java.lang.String val) {
        return new String(val);
    }

    public static Int intVal(int val) {
        return new Int(val);
    }

    /*
        Below are specific implementations of Val. Useful for being able to specify types of Val in parameters.
     */

    public static class Boolean extends Val<java.lang.Boolean> {

        private Boolean(boolean val) {
            super(val);
        }

        @Override
        public ValType type() {
            return ValType.BOOLEAN;
        }
    }

    public static class String extends Val<java.lang.String> {

        private String(java.lang.String val) {
            super(val);
        }

        @Override
        public ValType type() {
            return ValType.STRING;
        }
    }

    public static class Int extends Val<Integer> {

        private Int(int val) {
            super(val);
        }

        @Override
        public ValType type() {
            return ValType.INT;
        }

        public static Int random(int max) {
            return new Int(new Random().nextInt(max + 1));
        }

    }

}
