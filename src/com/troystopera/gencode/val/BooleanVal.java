package com.troystopera.gencode.val;

/**
 * Class used to virtually represent a boolean.
 */
public class BooleanVal extends Val<Boolean> {

    private static final BooleanVal falseVal = new BooleanVal(false);
    private static final BooleanVal trueVal = new BooleanVal(true);

    private BooleanVal(boolean val) {
        super(ValType.BOOLEAN, val);
    }

    public static BooleanVal of(boolean b) {
        return b ? trueVal : falseVal;
    }

}
