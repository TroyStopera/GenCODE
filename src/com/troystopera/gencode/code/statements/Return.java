package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/**
 * Represents a return statement.
 */
public class Return extends Statement<Val> {

    private final Evaluation evaluation;

    private Return(Evaluation evaluation) {
        super(Type.RETURN);
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Val> val;
        switch (evaluation.getEvalType()) {
            case COMPARISON:
                Comparison comparison = (Comparison) evaluation;
                val = control.execute(comparison, console, scope);
                if (val.isPresent()) console.setReturn(val.get());
                else console.setReturn(null);
                return val;
            case FUNC_CALL:
                FunctionCall functionCall = (FunctionCall) evaluation;
                val = control.execute(functionCall, console, scope);
                if (val.isPresent()) console.setReturn(val.get());
                else console.setReturn(null);
                return val;
            case OPERATION:
                Operation operation = (Operation) evaluation;
                val = control.execute(operation, console, scope);
                if (val.isPresent()) console.setReturn(val.get());
                else console.setReturn(null);
                return val;
            case VALUE:
                Value value = (Value) evaluation;
                val = control.execute(value, console, scope);
                if (val.isPresent()) console.setReturn(val.get());
                else console.setReturn(null);
                return val;
            case VARIABLE:
                Variable variable = (Variable) evaluation;
                val = control.execute(variable, console, scope);
                if (val.isPresent()) console.setReturn(val.get());
                else console.setReturn(null);
                return val;
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

    public static Return returnStmt(Val val) {
        return new Return(Value.of(val));
    }

    public static Return returnStmt(Evaluation evaluation) {
        return new Return(evaluation);
    }

}
