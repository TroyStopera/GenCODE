package com.troystopera.gencode.code.logic;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.val.BooleanVal;
import com.troystopera.gencode.val.Val;

import static com.troystopera.gencode.val.ValType.INT;

/***
 * Contains all logic for all supported comparisons.
 */
public class Comparisons {

    public static BooleanVal greaterThan(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() > v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>) type: " + v1.type));
        }
    }

    public static BooleanVal lessThan(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() < v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<) type: " + v1.type));
        }
    }

    public static BooleanVal equalTo(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() == v2.asInt());
            case STRING:
                return BooleanVal.of(v1.asString().equals(v2.asString()));
            case BOOLEAN:
                return BooleanVal.of(v1.asBoolean() == v2.asBoolean());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.type));
        }
    }

    public static BooleanVal notEqualTo(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() != v2.asInt());
            case STRING:
                return BooleanVal.of(!v1.asString().equals(v2.asString()));
            case BOOLEAN:
                return BooleanVal.of(v1.asBoolean() != v2.asBoolean());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.type));
        }
    }

    public static BooleanVal greaterThanEqual(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() >= v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>=) type: " + v1.type));
        }
    }

    public static BooleanVal lessThanEqual(Val v1, Val v2) {
        switch (v1.type) {
            case INT:
                return BooleanVal.of(v1.asInt() <= v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<=) type: " + v1.type));
        }
    }

}
