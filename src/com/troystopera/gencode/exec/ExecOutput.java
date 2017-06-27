package com.troystopera.gencode.exec;

import com.troystopera.gencode.val.Val;

import java.util.Collection;

/**
 * Data class for holding information about what the code outputted.
 */
public class ExecOutput {

    private final Collection<String> console;
    private Val returnVal;

    ExecOutput(Collection<String> console, Val returnVal) {
        this.console = console;
        this.returnVal = returnVal;
    }

    public Collection<String> getConsole() {
        return console;
    }

    public Val getReturnVal() {
        return returnVal;
    }

}
