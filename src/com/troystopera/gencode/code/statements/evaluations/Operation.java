package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.logic.Operations;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.PrimitiveVar;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * A class of Statement that changes the value of variables.
 */
public class Operation extends Evaluation {

    private final OperationType type;
    private final String varName;
    private final Evaluation evaluation;

    private Operation(OperationType type, String varName, Evaluation evaluation) {
        super(Type.OPERATION);
        this.type = type;
        this.varName = varName;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<PrimitiveVar> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> optVar = control.execute(evaluation, console, scope);
        if (!optVar.isPresent()) throw new GenerationException(new NullPointerException("Null value in operation"));

        Var var1 = scope.getVal(varName);
        Var var2 = optVar.get();

        if (!var1.getType().isPrimitive || !var2.getType().isPrimitive)
            throw new GenerationException(new IllegalArgumentException("Non-primitive var used in operation"));

        PrimitiveVar pVar1 = (PrimitiveVar) var1;
        PrimitiveVar pVar2 = (PrimitiveVar) var2;
        PrimitiveVar result;

        if (pVar1.getType() == pVar2.getType()) {
            switch (type) {
                case ADDITION:
                    result = Operations.add(pVar1, pVar2);
                    break;
                case SUBTRACTION:
                    result = Operations.subtract(pVar1, pVar2);
                    break;
                case MULTIPLICATION:
                    result = Operations.multiply(pVar1, pVar2);
                    break;
                case DIVISION:
                    result = Operations.divide(pVar1, pVar2);
                    break;
                case MODULUS:
                    result = Operations.modulo(pVar1, pVar2);
                    break;
                default:
                    throw new GenerationException(new UnsupportedOperationException("Unknown operation '" + type + "'"));
            }
        } else throw new GenerationException(new UnsupportedOperationException(
                "Incompatible types for operation (" + var1.getType() + ", " + var2.getType() + ")"));
        return Optional.of(result);
    }

    public OperationType getOpType() {
        return type;
    }

    public String getVarName() {
        return varName;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public static Operation addition(String name, String name1) {
        return new Operation(OperationType.ADDITION, name, Variable.of(name1));
    }

    public static Operation addition(String name, PrimitiveVar var) {
        return new Operation(OperationType.ADDITION, name, Value.of(var));
    }

    public static Operation subtraction(String name, String name1) {
        return new Operation(OperationType.SUBTRACTION, name, Variable.of(name1));
    }

    public static Operation subtraction(String name, PrimitiveVar var) {
        return new Operation(OperationType.SUBTRACTION, name, Value.of(var));
    }

    public static Operation multiplication(String name, String name1) {
        return new Operation(OperationType.MULTIPLICATION, name, Variable.of(name1));
    }

    public static Operation multiplication(String name, PrimitiveVar var) {
        return new Operation(OperationType.MULTIPLICATION, name, Value.of(var));
    }

    public static Operation division(String name, String name1) {
        return new Operation(OperationType.DIVISION, name, Variable.of(name1));
    }

    public static Operation division(String name, PrimitiveVar var) {
        return new Operation(OperationType.DIVISION, name, Value.of(var));
    }

    public static Operation modulus(String name, String name1) {
        return new Operation(OperationType.MODULUS, name, Variable.of(name1));
    }

    public static Operation modulus(String name, PrimitiveVar var) {
        return new Operation(OperationType.MODULUS, name, Value.of(var));
    }

}
