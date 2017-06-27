package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.val.IntVal;
import com.troystopera.gencode.val.Val;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.Operation;
import com.troystopera.gencode.code.statements.evaluations.Value;
import com.troystopera.gencode.code.statements.evaluations.Variable;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/***
 * Statement that assigns a value to a variable.
 */
public class Assignment extends Statement<Val> {

    private final String var;
    private final Evaluation evaluation;

    private Assignment(String var, Evaluation evaluation) {
        super(Statement.Type.ASSIGNMENT);
        this.var = var;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Val> val = control.execute(evaluation, console, scope);
        if (val.isPresent()) scope.updateVal(var, val.get());
        else scope.nullVar(var);
        //doesn't return a value, just sets it
        return Optional.empty();
    }

    public String getVar() {
        return var;
    }

    public Evaluation getEval() {
        return evaluation;
    }

    public static Assignment assign(String var, Evaluation evaluation) {
        return new Assignment(var, evaluation);
    }

    public static Assignment assign(String var, String var2) {
        return new Assignment(var, Variable.of(var2));
    }

    public static Assignment assign(String var, Val val) {
        return new Assignment(var, Value.of(val));
    }

    public static Assignment increment(String var) {
        Operation operation = Operation.addition(var, IntVal.of(1));
        return new Assignment(var, operation);
    }

    public static Assignment decrement(String var) {
        Operation operation = Operation.subtraction(var, IntVal.of(1));
        return new Assignment(var, operation);
    }

}
