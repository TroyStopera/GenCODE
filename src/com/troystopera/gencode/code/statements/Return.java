package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/**
 * Represents a return statement.
 */
public class Return extends Statement<Var> {

    private final Evaluation evaluation;

    private Return(Evaluation evaluation) {
        super(Type.RETURN);
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> var;
        switch (evaluation.getEvalType()) {
            case COMPARISON:
                Comparison comparison = (Comparison) evaluation;
                var = control.execute(comparison, console, scope);
                if (var.isPresent()) console.setReturn(var.get());
                else console.setReturn(null);
                return var;
            case FUNC_CALL:
                FunctionCall functionCall = (FunctionCall) evaluation;
                var = control.execute(functionCall, console, scope);
                if (var.isPresent()) console.setReturn(var.get());
                else console.setReturn(null);
                return var;
            case OPERATION:
                MathOperation mathOperation = (MathOperation) evaluation;
                var = control.execute(mathOperation, console, scope);
                if (var.isPresent()) console.setReturn(var.get());
                else console.setReturn(null);
                return var;
            case VALUE:
                Value value = (Value) evaluation;
                var = control.execute(value, console, scope);
                if (var.isPresent()) console.setReturn(var.get());
                else console.setReturn(null);
                return var;
            case VARIABLE:
                Variable variable = (Variable) evaluation;
                var = control.execute(variable, console, scope);
                if (var.isPresent()) console.setReturn(var.get());
                else console.setReturn(null);
                return var;
            default:
                throw new GenerationException(new IllegalArgumentException("Invalid return type: " + evaluation.getEvalType().name()));
        }
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public static Return returnStmt(String var) {
        return new Return(Variable.of(var));
    }

    public static Return returnStmt(Var var) {
        return new Return(Value.of(var));
    }

    public static Return returnStmt(Evaluation evaluation) {
        return new Return(evaluation);
    }

}
