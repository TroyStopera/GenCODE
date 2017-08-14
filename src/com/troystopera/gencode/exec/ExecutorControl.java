package com.troystopera.gencode.exec;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.var.BooleanVar;
import com.troystopera.gencode.var.Var;

import java.util.*;

/**
 * Class to control the flow of execution of generated code. Can also be sued to create false answers.
 */
public class ExecutorControl {

    private static final Random rand = new Random();
    private final Hashtable<Comparison, Integer> branchPoints = new Hashtable<>();

    private final Problem problem;
    private boolean genFalse = false;

    public ExecutorControl(Problem problem) {
        this.problem = problem;
    }

    @SuppressWarnings("unchecked")
    public ExecOutput getOutput() {
        genFalse = false;

        Console console = new Console();
        execute(problem.getMainFunction(), console, new Scope());
        return console.toOutput();
    }

    public List<ExecOutput> getFalseOutput(int count) {
        genFalse = true;
        branchPoints.clear();

        List<ExecOutput> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Console console = new Console();
            execute(problem.getMainFunction(), console, new Scope());
            results.add(console.toOutput());
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Optional<Var> execute(Executable executable, Console console, Scope scope) {
        Optional opt = executable.execute(this, console, scope);
        if (opt.isPresent() && opt.get() instanceof Var) return (Optional<Var>) opt;
        else return Optional.empty();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public Optional<BooleanVar> evaluate(Comparison comparison, Console console, Scope scope) {
        //mask it as Executable so we have aces to execute()
        Executable<BooleanVar> executable = comparison;
        Optional<BooleanVar> bool = executable.execute(this, console, scope);
        //if going for false result, then change results
        if (genFalse) {
            if (bool.isPresent()) {
                boolean result = bool.get().getValue();
                //determine what percentage to false branch
                int prob;
                if (branchPoints.containsKey(comparison)) {
                    prob = 25 - branchPoints.get(comparison);
                    branchPoints.put(comparison, branchPoints.get(comparison) + 2);
                } else {
                    prob = 75;
                    branchPoints.put(comparison, 1);
                }
                //with prob% chance, reverse the result
                if (rand.nextInt(100) < prob) return Optional.of(BooleanVar.of(!result));
            }
        }
        //otherwise just return
        return bool;
    }

}
