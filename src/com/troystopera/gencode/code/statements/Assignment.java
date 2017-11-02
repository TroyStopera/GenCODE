package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.MathOperation;
import com.troystopera.gencode.code.statements.evaluations.OperationType;
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
    private final int index;
    private final Evaluation evaluation;

    private Assignment(String var, int index, Evaluation evaluation) {
        super(Statement.Type.ASSIGNMENT);
        this.var = var;
        this.index = index;
        this.evaluation = evaluation;
    }

    public boolean isArrayIndexAssign() {
        return index != -1;
    }

    public int getArrayIndex() {
        return index;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> var = control.execute(evaluation, console, scope);
        if (var.isPresent()) {
            if (isArrayIndexAssign()) scope.setArrVar(this.var, index, var.get());
            else scope.setVar(this.var, var.get());
        } else {
            if (isArrayIndexAssign()) scope.nullArrVar(this.var, index);
            else scope.nullVar(this.var);
        }
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
        return new Assignment(name, -1, evaluation);
    }

    public static Assignment assign(String name, String var) {
        return new Assignment(name, -1, Variable.of(var));
    }

    public static Assignment assign(String name, Var var) {
        return new Assignment(name, -1, Value.of(var));
    }

    public static Assignment assignArray(String name, int index, Evaluation evaluation) {
        return new Assignment(name, index, evaluation);
    }

    public static Assignment assignArray(String name, int index, String var) {
        return new Assignment(name, index, Variable.of(var));
    }

    public static Assignment assignArray(String name, int index, Var var) {
        return new Assignment(name, index, Value.of(var));
    }

    public static Assignment increment(String name) {
        MathOperation mathOperation = new MathOperation(OperationType.ADDITION, name, IntVar.of(1));
        return new Assignment(name, -1, mathOperation);
    }

    public static Assignment decrement(String name) {
        MathOperation mathOperation = new MathOperation(OperationType.SUBTRACTION, name, IntVar.of(1));
        return new Assignment(name, -1, mathOperation);
    }

}
