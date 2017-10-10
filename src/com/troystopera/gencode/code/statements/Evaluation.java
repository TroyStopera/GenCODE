package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.var.Var;

/**
 * A Statement that can be evaluated to a Val.
 */
public abstract class Evaluation<T extends Var> extends Statement<T> {

    public enum Type {
        COMPARISON, FUNC_CALL, OPERATION, VALUE, VARIABLE, ARRAY_ACCESS, ARRAY_LENGTH
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
