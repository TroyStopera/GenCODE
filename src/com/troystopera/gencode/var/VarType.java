package com.troystopera.gencode.var;

/**
 * Created by troy on 7/29/17.
 */
public enum VarType {

    INT_ARRAY(false), BOOLEAN_ARRAY(false), STRING_ARRAY(false),
    INT_PRIMITIVE(true), BOOLEAN_PRIMITIVE(true), STRING_PRIMITIVE(true);

    public final boolean isPrimitive;

    VarType(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

}
