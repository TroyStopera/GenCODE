package com.troystopera.gencode.var;

/**
 * Created by troy on 7/29/17.
 */
public abstract class PrimitiveVar<T> extends Var {

    private final T value;

    protected PrimitiveVar(VarType type, T value) {
        super(type);
        this.value = value;
    }

    public final T getValue() {
        return value;
    }

    public boolean equals(PrimitiveVar primitiveVar) {
        return getType() == primitiveVar.getType() && value.equals(primitiveVar.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
