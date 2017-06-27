package com.troystopera.gencode.val;

/**
 * Class used to virtually represent a String.
 */
public class StringVal extends Val<String> {

    private StringVal(String val) {
        super(ValType.STRING, val);
    }

    public static StringVal of(String string) {
        return new StringVal(string);
    }

}
