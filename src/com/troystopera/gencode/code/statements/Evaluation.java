package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.Statement;

/**
 * A Statement that can be evaluated to a Val.
 */
public abstract class Evaluation<T extends Val> extends Statement<T> {

    public enum Type {
        COMPARISON, FUNC_CALL, OPERATION, VALUE, VARIABLE
    }

    private final Type type;

    public Evaluation(Type type) {
        super(Statement.Type.EVALUATION);
        this.type = type;
    }

    public final Type getEvalType() {
        return type;
    }

}
