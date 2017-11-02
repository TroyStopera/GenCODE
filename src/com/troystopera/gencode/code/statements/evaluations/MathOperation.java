package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.logic.Operations;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.PrimitiveVar;
import com.troystopera.gencode.var.Var;
import com.troystopera.gencode.var.VarType;

import java.util.Optional;

/***
 * A class of Statement that changes the value of variables.
 */
public class MathOperation extends Evaluation<IntVar> {

    private final OperationType type;
    private final String varName;
    private final Evaluation evaluation;

    public MathOperation(OperationType type, String varName, String varName2) {
        super(Type.OPERATION);
        this.type = type;
        this.varName = varName;
        this.evaluation = Variable.of(varName2);
    }

    public MathOperation(OperationType type, String varName, IntVar intVar) {
        super(Type.OPERATION);
        this.type = type;
        this.varName = varName;
        this.evaluation = Value.of(intVar);
    }

    @Override
    protected final Optional<IntVar> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> optVar = control.execute(evaluation, console, scope);
        if (!optVar.isPresent()) throw new GenerationException(new NullPointerException("Null value in operation"));

        Var var1 = scope.getVar(varName);
        Var var2 = optVar.get();

        if (var1.getType() != VarType.INT_PRIMITIVE || var2.getType() != VarType.INT_PRIMITIVE)
            throw new GenerationException(new IllegalArgumentException("Non-int var used in operation"));

        IntVar pVar1 = (IntVar) var1;
        IntVar pVar2 = (IntVar) var2;
        IntVar result;

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

}
