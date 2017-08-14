package com.troystopera.gencode.var;

import static com.troystopera.gencode.var.VarType.STRING_PRIMITIVE;

/**
 * Class used to virtually represent a String.
 */
public class StringVar extends PrimitiveVar<String> {

    private StringVar(String val) {
        super(STRING_PRIMITIVE, val);
    }

    public static StringVar of(String string) {
        return new StringVar(string);
    }

}
