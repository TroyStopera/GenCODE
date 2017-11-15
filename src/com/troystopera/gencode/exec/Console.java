package com.troystopera.gencode.exec;

import com.troystopera.gencode.var.Var;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Used during execution to record generated code's printed output and return values.
 */
public class Console {

    private Var returnVar;

    Console() {
        //only instantiated from this package
    }

    public void setReturn(Var var) {
        returnVar = var;
    }

    ExecOutput toOutput() {
        return new ExecOutput(returnVar);
    }

}
