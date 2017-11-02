package com.troystopera.gencode.code.statements;

import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.evaluations.Value;
import com.troystopera.gencode.code.statements.evaluations.Variable;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;
import com.troystopera.gencode.var.VarType;

import java.util.Optional;

/***
 * Represents a simple declaration of a variable.
 */
public class Declaration extends Statement<Var> {

    private final String var;
    private final VarType type;
    //optional assignment values
    private final Evaluation evaluation;

    private Declaration(String var, VarType type, Evaluation evaluation) {
        super(Statement.Type.DECLARATION);
        this.var = var;
        this.type = type;
        this.evaluation = evaluation;
    }

    public String getVarName() {
        return var;
    }

    public VarType getVarType() {
        return type;
    }

    public boolean hasAssignment() {
        return evaluation != null;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        scope.addVar(var, null);
        //execute assignment
        if (hasAssignment()) control.execute(Assignment.assign(var, evaluation), console, scope);
        //no logical value to return
        return Optional.empty();
    }

    public static Declaration declare(String name, VarType type) {
        return new Declaration(name, type, null);
    }

    public static Declaration declareWithAssign(String name, VarType type, String var) {
        return new Declaration(name, type, Variable.of(var));
    }

    public static Declaration declareWithAssign(String name, VarType type, Var var) {
        return new Declaration(name, type, Value.of(var));
    }

    public static Declaration declareWithAssign(String name, VarType type, Evaluation evaluation) {
        return new Declaration(name, type, evaluation);
    }

}
