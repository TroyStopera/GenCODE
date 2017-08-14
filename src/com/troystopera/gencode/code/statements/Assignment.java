package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.Operation;
import com.troystopera.gencode.code.statements.evaluations.Value;
import com.troystopera.gencode.code.statements.evaluations.Variable;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * Statement that assigns a value to a variable.
 */
public class Assignment extends Statement<Var> {

    private final String var;
    private final Evaluation evaluation;

    private Assignment(String var, Evaluation evaluation) {
        super(Statement.Type.ASSIGNMENT);
        this.var = var;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> var = control.execute(evaluation, console, scope);
        if (var.isPresent()) scope.updateVal(this.var, var.get());
        else scope.nullVar(this.var);
        //doesn't return a value, just sets it
        return Optional.empty();
    }

    public String getVar() {
        return var;
    }

    public Evaluation getEval() {
        return evaluation;
    }

    public static Assignment assign(String name, Evaluation evaluation) {
        return new Assignment(name, evaluation);
    }

    public static Assignment assign(String name, String var) {
        return new Assignment(name, Variable.of(var));
    }

    public static Assignment assign(String name, Var var) {
        return new Assignment(name, Value.of(var));
    }

    public static Assignment increment(String name) {
        Operation operation = Operation.addition(name, IntVar.of(1));
        return new Assignment(name, operation);
    }

    public static Assignment decrement(String name) {
        Operation operation = Operation.subtraction(name, IntVar.of(1));
        return new Assignment(name, operation);
    }

}
