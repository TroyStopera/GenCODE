package com.troystopera.gencode.code.components;

import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

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
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        if (isDoWhile) do {
            //make sure to create a new scope for code that executes within the while block
            Optional<Var> var = super.execute(control, console, scope.newChildScope());
            //stop execution at a return statement
            if (var.isPresent()) return var;
        } while (control.evaluate(comparison, console, scope).get().getValue());

        else while (control.evaluate(comparison, console, scope).get().getValue()) {
            //make sure to create a new scope for code that executes within the while block
            Optional<Var> var = super.execute(control, console, scope.newChildScope());
            //stop execution at a return statement
            if (var.isPresent()) return var;
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
