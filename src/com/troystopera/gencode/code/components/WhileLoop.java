package com.troystopera.gencode.code.components;

import com.troystopera.gencode.val.Val;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Optional;

/***
 * While loop component.
 */
public class WhileLoop extends CodeBlock {

    private final Comparison comparison;
    private final boolean isDoWhile;

    public WhileLoop(Comparison comparison) {
        this(comparison, false);
    }

    public WhileLoop(Comparison comparison, boolean isDoWhile) {
        super(Type.WHILE_LOOP);
        this.comparison = comparison;
        this.isDoWhile = isDoWhile;
    }

    @Override
    protected final Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        if (isDoWhile) do {
            //make sure to create a new scope for code that executes within the while block
            Optional<Val> val = super.execute(control, console, scope.newChildScope());
            //stop execution at a return statement
            if (val.isPresent()) return val;
        } while (control.evaluate(comparison, console, scope).get().val);

        else while (control.evaluate(comparison, console, scope).get().val) {
            //make sure to create a new scope for code that executes within the while block
            Optional<Val> val = super.execute(control, console, scope.newChildScope());
            //stop execution at a return statement
            if (val.isPresent()) return val;
        }
        return Optional.empty();
    }

    public Comparison getComparison() {
        return comparison;
    }

    public boolean isDoWhile() {
        return isDoWhile;
    }
}
