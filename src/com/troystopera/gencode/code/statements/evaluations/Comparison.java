package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.logic.Comparisons;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.BooleanVar;
import com.troystopera.gencode.var.IntVar;
import com.troystopera.gencode.var.PrimitiveVar;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * A class of Statement that compares values and variables.
 */
public class Comparison<T extends PrimitiveVar> extends Evaluation<BooleanVar> {

    private final ComparisonType type;
    private Evaluation<T> left, right;

    public Comparison(ComparisonType type, Evaluation<T> left, Evaluation<T> right) {
        super(Type.COMPARISON);
        this.type = type;
        this.left = left;
        this.right = right;
    }

    @Override
    protected final Optional<BooleanVar> execute(ExecutorControl control, Console console, Scope scope) {
        Optional<Var> leftOpt = control.execute(left, console, scope);
        Optional<Var> rightOpt = control.execute(right, console, scope);
        if (!rightOpt.isPresent() || !leftOpt.isPresent())
            throw new GenerationException(new NullPointerException("Null value in operation"));

        Var var1 = leftOpt.get();
        Var var2 = rightOpt.get();

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

    public Evaluation getLeft() {
        return left;
    }

    public Evaluation getRight() {
        return right;
    }

}
