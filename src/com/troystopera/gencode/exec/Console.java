package com.troystopera.gencode.exec;

import com.troystopera.gencode.Val;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used during execution to record generated code's printed output and return values.
 */
public class Console {

    private final Collection<String> console = new LinkedList<>();
    private Val returnVal;

    Console() {
        //only instantiated from this package
    }

    public void print(String s) {
        console.add(s);
    }

    public void setReturn(Val val) {
        returnVal = val;
    }

    ExecOutput toOutput() {
        return new ExecOutput(console, returnVal);
    }

}
