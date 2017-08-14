package com.troystopera.gencode.var;

import java.util.Random;

/**
 * Created by troy on 7/29/17.
 */
public abstract class Var {

    static final Random random = new Random();

    private final VarType type;

    protected Var(VarType type) {
        this.type = type;
    }

    public final VarType getType() {
        return type;
    }

}
