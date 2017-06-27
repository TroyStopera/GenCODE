package com.troystopera.gencode.val;

import java.util.Random;

/***
 * High-level representation of a value.
 * Allows for the generated code to use any data type in operations and variable declarations.
 */
public abstract class Val<T> {

    static final Random random = new Random();

    public final ValType type;
    public final T val;

    Val(ValType type, T val) {
        this.type = type;
        this.val = val;
    }

    public boolean equals(Val val) {
        return type == val.type && this.val.equals(val.val);
    }

    public boolean asBoolean() {
        if (val instanceof Boolean) return (Boolean) val;
        else return Boolean.valueOf(val.toString());
    }

    public java.lang.String asString() {
        if (val instanceof String) return (String) val;
        else return val.toString();
    }

    public int asInt() {
        if (val instanceof Integer) return (Integer) val;
        else return Integer.valueOf(val.toString());
    }

    @Override
    public String toString() {
        return val.toString();
    }

}
