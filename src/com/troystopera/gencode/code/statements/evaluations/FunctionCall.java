package com.troystopera.gencode.code.statements.evaluations;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.components.Function;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Console;
import com.troystopera.gencode.exec.ExecutorControl;
import com.troystopera.gencode.exec.Scope;
import com.troystopera.gencode.var.Var;

import java.util.Optional;

/***
 * A call to a function that may or may not return a value.
 */
public class FunctionCall extends Evaluation<Var> {

    private final String function;
    private final Evaluation[] args;

    private FunctionCall(String function, Evaluation[] args) {
        super(Type.FUNC_CALL);
        this.function = function;
        this.args = args;
    }

    public String getFunction() {
        return function;
    }

    public Evaluation[] getArgs() {
        return args;
    }

    @Override
    protected final Optional<Var> execute(ExecutorControl control, Console console, Scope scope) {
        if (scope.containsFunction(function)) {
            Function fn = scope.getFunction(function);
            Function.Argument[] argNames = fn.getArgs();

            //safety check
            if (args.length != argNames.length) {
                throw new GenerationException(new IllegalArgumentException("Wrong number of arguments passed to function"));
            }

            //set the new scope to have proper values for their arguments
            for (int i = 0; i < args.length; i++) {
                Optional<Var> var = control.execute(args[i], console, scope);
                if (var.isPresent()) scope.addVar(argNames[i].name, var.get());
            }

            return control.execute(fn, console, scope.newChildScope());
        } else return Optional.empty();
    }

    public static FunctionCall call(String function, Evaluation... args) {
        return new FunctionCall(function, args);
    }

    public static FunctionCall call(String function, Var... args) {
        Evaluation[] evaluations = new Evaluation[args.length];
        //convert to Evaluations
        for (int i = 0; i < args.length; i++)
            evaluations[i] = Value.of(args[i]);
        return new FunctionCall(function, evaluations);
    }

}
