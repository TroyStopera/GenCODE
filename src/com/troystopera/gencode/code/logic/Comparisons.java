package com.troystopera.gencode.code.logic;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.var.BooleanVar;
import com.troystopera.gencode.var.PrimitiveVar;

/***
 * Contains all logic for all supported comparisons.
 */
public class Comparisons {

    public static BooleanVar greaterThan(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of((int) v1.getValue() > (int) v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>) type: " + v1.getType()));
        }
    }

    public static BooleanVar lessThan(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of((int) v1.getValue() < (int) v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<) type: " + v1.getType()));
        }
    }

    public static BooleanVar equalTo(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of(v1.getValue().equals(v2.getValue()));
            case STRING_PRIMITIVE:
                return BooleanVar.of(v1.getValue().equals(v2.getValue()));
            case BOOLEAN_PRIMITIVE:
                return BooleanVar.of(v1.getValue().equals(v2.getValue()));
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.getType()));
        }
    }

    public static BooleanVar notEqualTo(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of(!v1.getValue().equals(v2.getValue()));
            case STRING_PRIMITIVE:
                return BooleanVar.of(!v1.getValue().equals(v2.getValue()));
            case BOOLEAN_PRIMITIVE:
                return BooleanVar.of(!v1.getValue().equals(v2.getValue()));
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.getType()));
        }
    }

    public static BooleanVar greaterThanEqual(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of((int) v1.getValue() >= (int) v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>=) type: " + v1.getType()));
        }
    }

    public static BooleanVar lessThanEqual(PrimitiveVar v1, PrimitiveVar v2) {
        switch (v1.getType()) {
            case INT_PRIMITIVE:
                return BooleanVar.of((int) v1.getValue() <= (int) v2.getValue());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<=) type: " + v1.getType()));
        }
    }

}
