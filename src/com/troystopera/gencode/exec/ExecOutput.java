package com.troystopera.gencode.exec;

import com.troystopera.gencode.var.Var;

import java.util.Collection;
import java.util.Collections;

/**
 * Data class for holding information about what the code outputted.
 */
public class ExecOutput {

    private Var returnVar;

    ExecOutput(Var returnVar) {
        this.returnVar = returnVar;
    }

    public Var getReturnVar() {
        return returnVar;
    }

    @Override
    public int hashCode() {
        return returnVar.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExecOutput) {
            ExecOutput o = (ExecOutput) obj;
            return returnVar.equals(o.returnVar);
        }
        return false;
    }

}
