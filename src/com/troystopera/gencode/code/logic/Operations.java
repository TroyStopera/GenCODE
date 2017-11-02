package com.troystopera.gencode.code.logic;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.PrimitiveVar;
import com.troystopera.gencode.var.StringVar;

/***
 * Contains all logic for all supported operations.
 */
public class Operations {

    @SuppressWarnings("unchecked")
    public static <T extends PrimitiveVar> T add(T v1, T v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return (T) IntVar.of((int) v1.getValue() + (int) v2.getValue());
            case STRING_PRIMITIVE:
                return (T) StringVar.of(v1.getValue().toString() + v2.getValue().toString());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot add type: " + v1.getType()));
        }
    }

    public static IntVar subtract(IntVar v1, IntVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return IntVar.of(v1.getValue() - v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot subtract type: " + v1.getType()));
        }
    }

    public static IntVar multiply(IntVar v1, IntVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return IntVar.of(v1.getValue() * v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot multiply type: " + v1.getType()));
        }
    }

    public static IntVar divide(IntVar v1, IntVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return IntVar.of(v1.getValue() / v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot divide type: " + v1.getType()));
        }
    }

    public static IntVar modulo(IntVar v1, IntVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return IntVar.of(v1.getValue() % v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot modulo type: " + v1.getType()));
        }
    }

}
