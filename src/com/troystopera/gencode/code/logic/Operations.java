package com.troystopera.gencode.code.logic;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.val.IntVal;
import com.troystopera.gencode.val.StringVal;
import com.troystopera.gencode.val.Val;

/***
 * Contains all logic for all supported operations.
 */
public class Operations {

    public static Val add(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return IntVal.of(v1.asInt() + v2.asInt());
            case STRING:
                return StringVal.of(v1.asString() + v2.asString());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot add type: " + v1.type));
        }
    }

    public static Val subtract(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return IntVal.of(v1.asInt() - v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot subtract type: " + v1.type));
        }
    }

    public static Val multiply(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return IntVal.of(v1.asInt() * v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot multiply type: " + v1.type));
        }
    }

    public static Val divide(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return IntVal.of(v1.asInt() / v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot divide type: " + v1.type));
        }
    }

    public static Val modulo(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return IntVal.of(v1.asInt() % v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot modulo type: " + v1.type));
        }
    }

}
