package com.troystopera.gencode.var;

import static com.troystopera.gencode.var.VarType.BOOLEAN_PRIMITIVE;

/**
 * Class used to virtually represent a boolean.
 */
public class BooleanVar extends PrimitiveVar<Boolean> {

    private static final BooleanVar falseVal = new BooleanVar(false);
    private static final BooleanVar trueVal = new BooleanVar(true);

    private BooleanVar(boolean val) {
        super(BOOLEAN_PRIMITIVE, val);
    }

    public static BooleanVar of(boolean b) {
        return b ? trueVal : falseVal;
    }

}
