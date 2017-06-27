package com.troystopera.gencode.code.statements.evaluations;

import java.util.Random;

public enum ComparisonType {

    GREATER_THEN, LESS_THAN, EQUAL_TO, NOT_EQUAL_TO, GREATER_THEN_EQUAL, LESS_THAN_EQUAL;

    private static final Random random = new Random();

    public static ComparisonType randomType() {
        return values()[random.nextInt(values().length)];
    }

}
