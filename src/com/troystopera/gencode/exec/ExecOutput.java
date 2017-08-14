package com.troystopera.gencode.exec;

import com.troystopera.gencode.var.Var;

import java.util.Collection;

/**
 * Data class for holding information about what the code outputted.
 */
public class ExecOutput {

    private final Collection<String> console;
    private Var returnVar;

    ExecOutput(Collection<String> console, Var returnVar) {
        this.console = console;
        this.returnVar = returnVar;
    }

    public Collection<String> getConsole() {
        return console;
    }

    public Var getReturnVar() {
        return returnVar;
    }

}
