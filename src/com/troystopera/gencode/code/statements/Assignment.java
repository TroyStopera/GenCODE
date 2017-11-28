package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.GenerationException;
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
    private final Evaluation<IntVar> indexEval;
    private final Evaluation evaluation;

    private Assignment(String var, Evaluation<IntVar> indexEval, Evaluation evaluation) {
        super(Statement.Type.ASSIGNMENT);
        this.var = var;
        this.indexEval = indexEval;
        this.evaluation = evaluation;
    }

    public boolean isArrayIndexAssign() {
        return indexEval != null;
    }

    public Evaluation getArrayIndex() {
        return indexEval;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> var = control.execute(evaluation, console, scope);

        if (isArrayIndexAssign()) {
            Optional<Var> index = control.execute(indexEval, console, scope);
            if (!index.isPresent()) throw new GenerationException(new NullPointerException());
            if (var.isPresent()) {
                scope.setArrVar(this.var, ((IntVar) index.get()).getValue(), var.get());
            } else {
                scope.nullArrVar(this.var, ((IntVar) index.get()).getValue());
            }
        } else {
            if (var.isPresent()) {
                scope.setVar(this.var, var.get());
            } else {
                scope.nullVar(this.var);
            }
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
        return new Assignment(name, null, evaluation);
    }

    public static Assignment assign(String name, String var) {
        return new Assignment(name, null, Variable.of(var));
    }

    public static Assignment assign(String name, Var var) {
        return new Assignment(name, null, Value.of(var));
    }

    public static Assignment assignArray(String name, Evaluation<IntVar> index, Evaluation evaluation) {
        return new Assignment(name, index, evaluation);
    }

    public static Assignment assignArray(String name, Evaluation<IntVar> index, String var) {
        return new Assignment(name, index, Variable.of(var));
    }

    public static Assignment assignArray(String name, Evaluation<IntVar> index, Var var) {
        return new Assignment(name, index, Value.of(var));
    }

    public static Assignment increment(String name) {
        MathOperation mathOperation = new MathOperation(OperationType.ADDITION, name, IntVar.of(1));
        return new Assignment(name, null, mathOperation);
    }

    public static Assignment decrement(String name) {
        MathOperation mathOperation = new MathOperation(OperationType.SUBTRACTION, name, IntVar.of(1));
        return new Assignment(name, null, mathOperation);
    }

}
