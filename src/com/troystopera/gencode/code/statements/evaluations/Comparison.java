package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.logic.Comparisons;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/***
 * A class of Statement that compares values and variables.
 */
public class Comparison extends Evaluation {

    private final ComparisonType type;
    private final String var;
    private Evaluation evaluation;

    private Comparison(ComparisonType type, String var1, Evaluation evaluation) {
        super(Type.COMPARISON);
        this.type = type;
        this.var = var1;
        this.evaluation = evaluation;
    }

    @Override
    protected final Optional<Val.Boolean> execute(ExecutorControl control, Console console, Scope scope) {
        Val val1 = scope.getVal(var);

        if (evaluation.getEvalType() == Type.OPERATION)
            throw new GenerationException(new IllegalArgumentException("Operation in Comparison"));

        Optional<Val> optVal = control.execute(evaluation, console, scope);
        if (!optVal.isPresent()) throw new GenerationException(new NullPointerException("Null value in comparison"));
        Val val2 = optVal.get();

        if (val1.type() == val2.type()) {
            switch (type) {
                case GREATER_THEN:
                    return Optional.of(Comparisons.greaterThan(val1, val2));
                case LESS_THAN:
                    return Optional.of(Comparisons.lessThan(val1, val2));
                case EQUAL_TO:
                    return Optional.of(Comparisons.equalTo(val1, val2));
                case NOT_EQUAL_TO:
                    return Optional.of(Comparisons.notEqualTo(val1, val2));
                case GREATER_THEN_EQUAL:
                    return Optional.of(Comparisons.greaterThanEqual(val1, val2));
                case LESS_THAN_EQUAL:
                    return Optional.of(Comparisons.lessThanEqual(val1, val2));
                default:
                    throw new GenerationException(new UnsupportedOperationException("Unknown operation '" + type + "'"));
            }
        } else throw new GenerationException(new UnsupportedOperationException(
                "Incompatible types for comparison (" + val1.type() + ", " + val2.type() + ")"));
    }

    public ComparisonType getCompType() {
        return type;
    }

    public String getVar() {
        return var;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public static Comparison greaterThan(String var1, String var2) {
        return new Comparison(ComparisonType.GREATER_THEN, var1, Variable.of(var2));
    }

    public static Comparison greaterThan(String var1, Val val) {
        return new Comparison(ComparisonType.GREATER_THEN, var1, Value.of(val));
    }

    public static Comparison lessThan(String var1, String var2) {
        return new Comparison(ComparisonType.LESS_THAN, var1, Variable.of(var2));
    }

    public static Comparison lessThan(String var1, Val val) {
        return new Comparison(ComparisonType.LESS_THAN, var1, Value.of(val));
    }

    public static Comparison equalTo(String var1, String var2) {
        return new Comparison(ComparisonType.EQUAL_TO, var1, Variable.of(var2));
    }

    public static Comparison equalTo(String var1, Val val) {
        return new Comparison(ComparisonType.EQUAL_TO, var1, Value.of(val));
    }

    public static Comparison notEqualTo(String var1, String var2) {
        return new Comparison(ComparisonType.NOT_EQUAL_TO, var1, Variable.of(var2));
    }

    public static Comparison notEqualTo(String var1, Val val) {
        return new Comparison(ComparisonType.NOT_EQUAL_TO, var1, Value.of(val));
    }

    public static Comparison greaterThanEqual(String var1, String var2) {
        return new Comparison(ComparisonType.GREATER_THEN_EQUAL, var1, Variable.of(var2));
    }

    public static Comparison greaterThanEqual(String var1, Val val) {
        return new Comparison(ComparisonType.GREATER_THEN_EQUAL, var1, Value.of(val));
    }

    public static Comparison lessThanEqual(String var1, String var2) {
        return new Comparison(ComparisonType.LESS_THAN_EQUAL, var1, Variable.of(var2));
    }

    public static Comparison lessThanEqual(String var1, Val val) {
        return new Comparison(ComparisonType.LESS_THAN_EQUAL, var1, Value.of(val));
    }

}
