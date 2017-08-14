package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.logic.Comparisons;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.BooleanVar;
import com.troystopera.gencode.var.PrimitiveVar;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * A class of Statement that compares values and variables.
 */
public class Comparison extends Evaluation {

    private final ComparisonType type;
    private final String varName;
    private Evaluation evaluation;

    private Comparison(ComparisonType type, String varName, Evaluation evaluation) {
        super(Type.COMPARISON);
        this.type = type;
        this.varName = varName;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<BooleanVar> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> optVar = control.execute(evaluation, console, scope);
        if (!optVar.isPresent()) throw new GenerationException(new NullPointerException("Null value in operation"));

        Var var1 = scope.getVal(varName);
        Var var2 = optVar.get();

        if (!var1.getType().isPrimitive || !var2.getType().isPrimitive)
            throw new GenerationException(new IllegalArgumentException("Non-primitive var used in comparison"));

        PrimitiveVar pVar1 = (PrimitiveVar) var1;
        PrimitiveVar pVar2 = (PrimitiveVar) var2;

        if (pVar1.getType() == pVar2.getType()) {
            switch (type) {
                case GREATER_THEN:
                    return Optional.of(Comparisons.greaterThan(pVar1, pVar2));
                case LESS_THAN:
                    return Optional.of(Comparisons.lessThan(pVar1, pVar2));
                case EQUAL_TO:
                    return Optional.of(Comparisons.equalTo(pVar1, pVar2));
                case NOT_EQUAL_TO:
                    return Optional.of(Comparisons.notEqualTo(pVar1, pVar2));
                case GREATER_THEN_EQUAL:
                    return Optional.of(Comparisons.greaterThanEqual(pVar1, pVar2));
                case LESS_THAN_EQUAL:
                    return Optional.of(Comparisons.lessThanEqual(pVar1, pVar2));
                default:
                    throw new GenerationException(new UnsupportedOperationException("Unknown operation '" + type + "'"));
            }
        } else throw new GenerationException(new UnsupportedOperationException(
                "Incompatible types for comparison (" + pVar1.getType() + ", " + pVar2.getType() + ")"));
    }

    public ComparisonType getCompType() {
        return type;
    }

    public String getVarName() {
        return varName;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public static Comparison greaterThan(String name, String name1) {
        return new Comparison(ComparisonType.GREATER_THEN, name, Variable.of(name1));
    }

    public static Comparison greaterThan(String name, Var var) {
        return new Comparison(ComparisonType.GREATER_THEN, name, Value.of(var));
    }

    public static Comparison lessThan(String name, String name1) {
        return new Comparison(ComparisonType.LESS_THAN, name, Variable.of(name1));
    }

    public static Comparison lessThan(String name, Var var) {
        return new Comparison(ComparisonType.LESS_THAN, name, Value.of(var));
    }

    public static Comparison equalTo(String name, String name1) {
        return new Comparison(ComparisonType.EQUAL_TO, name, Variable.of(name1));
    }

    public static Comparison equalTo(String name, Var var) {
        return new Comparison(ComparisonType.EQUAL_TO, name, Value.of(var));
    }

    public static Comparison notEqualTo(String name, String name1) {
        return new Comparison(ComparisonType.NOT_EQUAL_TO, name, Variable.of(name1));
    }

    public static Comparison notEqualTo(String name, Var var) {
        return new Comparison(ComparisonType.NOT_EQUAL_TO, name, Value.of(var));
    }

    public static Comparison greaterThanEqual(String name, String name1) {
        return new Comparison(ComparisonType.GREATER_THEN_EQUAL, name, Variable.of(name1));
    }

    public static Comparison greaterThanEqual(String name, Var var) {
        return new Comparison(ComparisonType.GREATER_THEN_EQUAL, name, Value.of(var));
    }

    public static Comparison lessThanEqual(String name, String name1) {
        return new Comparison(ComparisonType.LESS_THAN_EQUAL, name, Variable.of(name1));
    }

    public static Comparison lessThanEqual(String name, Var var) {
        return new Comparison(ComparisonType.LESS_THAN_EQUAL, name, Value.of(var));
    }

}
