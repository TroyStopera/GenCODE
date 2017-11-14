package com.troystopera.gencode.generator;

import java.util.Arrays;

/*
    This class is intended to generate unique variables for problems. A single instance will not generate overlapping
    variables, however two instances will generate the same variables in the same order.
        The order of vars is as follows: a, b, c... aa, ab, ac... ba, bb, bc...
 */
public class VarNameProvider {

    private char[] prefix = new char[0];
    private char nextVarChar = 'a';

    public String nextVar() {
        //generate the variable first, then adjust to next values accordingly
        String nextVar = (prefix.length == 0 ? "" : new String(prefix)) + Character.toString(nextVarChar);

        //if the next var char is z, then it needs reset to a and the prefix needs updated
        if (nextVarChar == 'z') {
            updatePrefix();
            nextVarChar = 'a';
        } else nextVarChar++;
        return nextVar;
    }

    private void updatePrefix() {
        //if there has been no prefix until now, create a prefix, otherwise increment and adjust
        if (prefix.length == 0) prefix = new char['a'];
        else {
            //loop from the end of the array to the start, looking for anything less than z
            for (int i = prefix.length - 1; i >= 0; i--) {
                if (prefix[i] < 'z') {
                    prefix[i]++;
                    //update everything TO THE RIGHT of this index with 'a'
                    for (int j = prefix.length - 1; j > i; j--) prefix[j] = 'a';
                    return;
                }
            }
            //if nothing was found less than z, create a new prefix one larger than before
            prefix = new char[prefix.length + 1];
            Arrays.fill(prefix, 'a');
        }
    }

    public boolean equals(VarNameProvider provider) {
        return nextVarChar == provider.nextVarChar && Arrays.equals(prefix, provider.prefix);
    }

    public VarNameProvider copy() {
        VarNameProvider provider = new VarNameProvider();
        char[] newPrefix = new char[prefix.length];
        System.arraycopy(prefix, 0, newPrefix, 0, prefix.length);
        provider.prefix = newPrefix;
        provider.nextVarChar = nextVarChar;
        return provider;
    }

}