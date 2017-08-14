package com.troystopera.gencode.exec;

import com.troystopera.gencode.var.Var;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used during execution to record generated code's printed output and return values.
 */
public class Console {

    private final Collection<String> console = new LinkedList<>();
    private Var returnVar;

    Console() {
        //only instantiated from this package
    }

    public void print(String s) {
        console.add(s);
    }

    public void setReturn(Var var) {
        returnVar = var;
    }

    ExecOutput toOutput() {
        return new ExecOutput(console, returnVar);
    }

}
