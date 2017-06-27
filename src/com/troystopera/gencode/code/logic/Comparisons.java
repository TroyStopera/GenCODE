package com.troystopera.gencode.code.logic;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.Val;

/***
 * Contains all logic for all supported comparisons.
 */
public class Comparisons {

    public static Val.Boolean greaterThan(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() > v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>) type: " + v1.type()));
        }
    }

    public static Val.Boolean lessThan(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() < v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<) type: " + v1.type()));
        }
    }

    public static Val.Boolean equalTo(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() == v2.asInt());
            case STRING:
                return Val.booleanVal(v1.asString().equals(v2.asString()));
            case BOOLEAN:
                return Val.booleanVal(v1.asBoolean() == v2.asBoolean());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.type()));
        }
    }

    public static Val.Boolean notEqualTo(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() != v2.asInt());
            case STRING:
                return Val.booleanVal(!v1.asString().equals(v2.asString()));
            case BOOLEAN:
                return Val.booleanVal(v1.asBoolean() != v2.asBoolean());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (==) type: " + v1.type()));
        }
    }

    public static Val.Boolean greaterThanEqual(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() >= v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (>=) type: " + v1.type()));
        }
    }

    public static Val.Boolean lessThanEqual(Val v1, Val v2) {
        switch (v1.type()) {
            case INT:
                return Val.booleanVal(v1.asInt() <= v2.asInt());
            default:
                throw new GenerationException(new UnsupportedOperationException("Cannot compare (<=) type: " + v1.type()));
        }
    }

}
