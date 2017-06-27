package com.troystopera.gencode.exec;

import java.util.Optional;

/***
 * Represents a class that can be executed on the Generation Virtual Machine for creation of answers.
 */
public abstract class Executable<T> {

    /*
        Method called when executing generated code. Can return anything to accommodate different executions.
     */
    protected abstract Optional<T> execute(ExecutorControl control, Console console, Scope scope);

    /*
        Returns the Type of the Executable.
     */
    public abstract Type getExecType();

    public enum Type {
        STATEMENT, COMPONENT, EMPTY
    }

}
