package com.troystopera.gencode.code.statements.evaluations;

import java.util.Random;

public enum OperationType {

    ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, MODULUS;

    private static final Random random = new Random();

    public static OperationType randomType() {
        return values()[random.nextInt(values().length)];
    }

}
