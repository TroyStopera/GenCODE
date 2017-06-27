package com.troystopera.gencode.code.components;

import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.Component;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.Executable;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/***
 * Simple Component to represent a set of Executables.
 */
public class CodeBlock extends Component {

    private final List<Executable> executables = new LinkedList<>();

    public CodeBlock(Executable... executables) {
        super(Type.GENERIC);
        Collections.addAll(this.executables, executables);
    }

    public CodeBlock(CodeBlock block) {
        super(Type.GENERIC);
        executables.addAll(block.getExecutables());
    }

    protected CodeBlock(Type type) {
        super(type);
    }

    public final void addExecutable(Executable executable) {
        executables.add(executable);
    }

    public final void addExecutable(Executable executable, int index) {
        executables.add(index, executable);
    }

    public final void addExecutables(Executable... executables) {
        Collections.addAll(this.executables, executables);
    }

    public final void addExecutables(List<Executable> executables) {
        this.executables.addAll(executables);
    }

    public final List<Executable> getExecutables() {
        return executables;
    }

    public final CodeBlock asCodeBlock() {
        CodeBlock block = new CodeBlock();
        block.addExecutables(executables);
        return block;
    }

    @Override
    protected Optional<Val> execute(ExecutorControl control, Console console, Scope scope) {
        for (Executable executable : executables) {
            //stop execution at a return statement
            if (executable instanceof Return) {
                Return r = (Return) executable;
                return control.execute(r, console, scope);
            } else {
                Optional optional = control.execute(executable, console, scope);
                //only *components* with a return value need to be considered for return
                if (executable instanceof Component && optional.isPresent()) {
                    Object val = optional.get();
                    if (val instanceof Val) return Optional.of((Val) val);
                }
            }
        }
        return Optional.empty();
    }

}
