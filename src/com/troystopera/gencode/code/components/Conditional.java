package com.troystopera.gencode.code.components;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.Component;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.BooleanVar;
import com.troystopera.gencode.var.Var;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/***
 * A series of Conditional branches.
 */
public class Conditional extends Component {

    private final Map<Comparison, CodeBlock> branches = new HashMap<>();
    private CodeBlock elseBlock;

    public Conditional() {
        super(Type.CONDITIONAL);
    }

    public Conditional addBranch(Comparison comparison, CodeBlock block) {
        branches.put(comparison, block);
        return this;
    }

    public Conditional setElse(CodeBlock elseBlock) {
        this.elseBlock = elseBlock;
        return this;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        for (Map.Entry<Comparison, CodeBlock> entry : branches.entrySet()) {
            Optional<BooleanVar> result = control.evaluate(entry.getKey(), console, scope);
            if (result.isPresent()) {
                //make sure to create a new record for code that executes within the conditional block
                if (result.get().getValue())
                    return control.execute(entry.getValue(), console, scope.createChildScope());
            }
        }
        if (elseBlock != null) {
            if (branches.isEmpty())
                throw new GenerationException(new IllegalStateException("Conditional was generated with only an else block"));
            //make sure to create a new record for code that executes within the else block
            return control.execute(elseBlock, console, scope.createChildScope());
        }
        return Optional.empty();
    }

    public Collection<Map.Entry<Comparison, CodeBlock>> getBranches() {
        return branches.entrySet();
    }

    public boolean hasElseBlock() {
        return elseBlock != null;
    }

    public CodeBlock getElseBlock() {
        return elseBlock;
    }

}
