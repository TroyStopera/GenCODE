package com.troystopera.gencode.code.components;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.code.statements.evaluations.Variable;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * For loop component.
 */
public class ForLoop extends CodeBlock {

    private final Comparison comparison;
    private final Assignment opAssignment;
    //one or the other, for either using a var from current record or creating one in loop declaration
    private final Assignment assignment;
    private final Declaration declaration;

    public ForLoop(Assignment assignment, Comparison comparison, Assignment opAssignment) {
        super(Type.FOR_LOOP);
        declaration = null;
        this.assignment = assignment;
        this.comparison = comparison;
        this.opAssignment = opAssignment;
    }

    public ForLoop(Declaration declaration, Comparison comparison, Assignment opAssignment) {
        super(Type.FOR_LOOP);
        assignment = null;
        this.declaration = declaration;
        this.comparison = comparison;
        this.opAssignment = opAssignment;
        if (!declaration.hasAssignment())
            throw new GenerationException(new IllegalArgumentException("For loop variable needs instantiated"));
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        if (assignment != null) control.execute(assignment, console, scope);
        else control.execute(declaration, console, scope);

        while (control.evaluate(comparison, console, scope).get().getValue()) {
            //make sure to create a new record for code that executes within the loop block
            Optional<Var> var = super.execute(control, console, scope.createChildScope());
            control.execute(opAssignment, console, scope);
            //stop execution at a return statement
            if (var.isPresent()) return var;
        }
        return Optional.empty();
    }

    public boolean hasDeclaration() {
        return declaration != null;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public Assignment getOpAssignment() {
        return opAssignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Declaration getDeclaration() {
        return declaration;
    }
}
