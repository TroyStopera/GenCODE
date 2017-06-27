package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.val.Val;
import com.troystopera.gencode.val.ValType;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.Value;
import com.troystopera.gencode.code.statements.evaluations.Variable;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/***
 * Represents a simple declaration of a variable.
 */
public class Declaration extends Statement<Val> {

    private final String var;
    private final ValType type;
    //optional assignment values
    private final Evaluation evaluation;

    private Declaration(String var, ValType type, Evaluation evaluation) {
        super(Statement.Type.DECLARATION);
        this.var = var;
        this.type = type;
        this.evaluation = evaluation;
    }

    public String getVarName() {
        return var;
    }

    public ValType getValType() {
        return type;
    }

    public boolean hasAssignment() {
        return evaluation != null;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    @Override
    protected final Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        scope.addVar(var);
        //execute assignment
        if (hasAssignment()) control.execute(Assignment.assign(var, evaluation), console, scope);
        //no logical value to return
        return Optional.empty();
    }

    public static Declaration declare(String var, ValType type) {
        return new Declaration(var, type, null);
    }

    public static Declaration declareWithAssign(String var, ValType type, String var2) {
        return new Declaration(var, type, Variable.of(var2));
    }

    public static Declaration declareWithAssign(String var, ValType type, Val val) {
        return new Declaration(var, type, Value.of(val));
    }

    public static Declaration declareWithAssign(String var, ValType type, Evaluation evaluation) {
        return new Declaration(var, type, evaluation);
    }

}
