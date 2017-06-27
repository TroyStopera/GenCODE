package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.val.Val;
import com.troystopera.gencode.code.logic.Operations;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/***
 * A class of Statement that changes the value of variables.
 */
public class Operation extends Evaluation {

    private final OperationType type;
    private final String var;
    private final Evaluation evaluation;

    private Operation(OperationType type, String var, Evaluation evaluation) {
        super(Type.OPERATION);
        this.type = type;
        this.var = var;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        Val val1 = scope.getVal(var);

        if (evaluation.getEvalType() == Type.COMPARISON)
            throw new GenerationException(new IllegalArgumentException("Comparison in Operation"));

        Optional<Val> optVal = control.execute(evaluation, console, scope);
        if (!optVal.isPresent()) throw new GenerationException(new NullPointerException("Null value in operation"));
        Val val2 = optVal.get();

        Val result;
        if (val1.type == val2.type) {
            switch (type) {
                case ADDITION:
                    result = Operations.add(val1, val2);
                    break;
                case SUBTRACTION:
                    result = Operations.subtract(val1, val2);
                    break;
                case MULTIPLICATION:
                    result = Operations.multiply(val1, val2);
                    break;
                case DIVISION:
                    result = Operations.divide(val1, val2);
                    break;
                case MODULUS:
                    result = Operations.modulo(val1, val2);
                    break;
                default:
                    throw new GenerationException(new UnsupportedOperationException("Unknown operation '" + type + "'"));
            }
        } else throw new GenerationException(new UnsupportedOperationException(
                "Incompatible types for operation (" + val1.type + ", " + val2.type + ")"));
        return Optional.of(result);
    }

    public OperationType getOpType() {
        return type;
    }

    public String getVar() {
        return var;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public static Operation addition(String var, String var2) {
        return new Operation(OperationType.ADDITION, var, Variable.of(var2));
    }

    public static Operation addition(String var, Val val) {
        return new Operation(OperationType.ADDITION, var, Value.of(val));
    }

    public static Operation subtraction(String var, String var2) {
        return new Operation(OperationType.SUBTRACTION, var, Variable.of(var2));
    }

    public static Operation subtraction(String var, Val val) {
        return new Operation(OperationType.SUBTRACTION, var, Value.of(val));
    }

    public static Operation multiplication(String var, String var2) {
        return new Operation(OperationType.MULTIPLICATION, var, Variable.of(var2));
    }

    public static Operation multiplication(String var, Val val) {
        return new Operation(OperationType.MULTIPLICATION, var, Value.of(val));
    }

    public static Operation division(String var, String var2) {
        return new Operation(OperationType.DIVISION, var, Variable.of(var2));
    }

    public static Operation division(String var, Val val) {
        return new Operation(OperationType.DIVISION, var, Value.of(val));
    }

    public static Operation modulus(String var, String var2) {
        return new Operation(OperationType.MODULUS, var, Variable.of(var2));
    }

    public static Operation modulus(String var, Val val) {
        return new Operation(OperationType.MODULUS, var, Value.of(val));
    }

}
