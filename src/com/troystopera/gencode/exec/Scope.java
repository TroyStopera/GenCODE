package com.troystopera.gencode.exec;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.Val;
import com.troystopera.gencode.code.components.Function;

import java.util.*;

/***
 * Representation of a scope. It maintains a list of all variables declared within it, as well as the values of
 * variables for use when executing the code to generate answers to questions.
 */
public class Scope {

    private static final Random random = new Random();

    private final Map<String, Val> vars = new HashMap<>();
    private final List<String> varNames = new ArrayList<>();
    private final Map<String, Function> funcs = new HashMap<>();
    private final List<String> funcNames = new ArrayList<>();
    private final Scope parent;
    private final int depth;

    private OutputListener listener;

    public Scope() {
        this(null, 0);
    }

    private Scope(Scope parent, int depth) {
        this.parent = parent;
        this.depth = depth;
    }

    public String getRandVarLimited() {
        return varNames.get(random.nextInt(varNames.size()));
    }

    public String getRandVar() {
        return getRandVar(depth);
    }

    public String getRandVar(int range) {
        return getParent(random.nextInt(range)).getRandVarLimited();
    }

    public boolean containsVarLimited(String var) {
        return varNames.contains(var);
    }

    public boolean containsVar(String var) {
        if (varNames.contains(var)) return true;
        else return hasParent() && getParent().containsVar(var);
    }

    public boolean containsFunction(String name) {
        return funcs.containsKey(name);
    }


    public boolean hasParent() {
        return parent != null;
    }

    public Scope getParent() {
        return parent;
    }

    public Scope getParent(int up) {
        Scope scope = this;
        for (int i = 0; i < up && scope.hasParent(); i++) {
            scope = scope.getParent();
        }
        return scope;
    }

    public int getScopeDepth() {
        return depth;
    }

    public void addVar(String var) {
        addVar(var, null);
    }

    public void addVar(String var, Val val) {
        varNames.add(var);
        vars.put(var, val);
    }

    public void nullVar(String var) {
        vars.remove(var);
    }

    public Val getVal(String var) {
        Scope scope = this;
        while (!scope.vars.containsKey(var) && scope.hasParent()) scope = scope.getParent();
        if (scope.vars.containsKey(var)) return scope.vars.get(var);
        throw new GenerationException(new NullPointerException("Unknown variable: " + var));
    }

    public void updateVal(String var, Val val) {
        Scope scope = this;
        while (!scope.varNames.contains(var) && scope.hasParent()) scope = scope.getParent();
        if (scope.varNames.contains(var)) scope.vars.put(var, val);
        else throw new GenerationException(new NullPointerException("Unknown variable: " + var));
    }

    public Function getFunction(String name) {
        return funcs.get(name);
    }

    public void addFunction(String name, Function function) {
        funcNames.add(name);
        funcs.put(name, function);
    }

    public Scope newChildScope() {
        return new Scope(this, depth + 1);
    }

    public void setOutputListener(OutputListener listener) {
        this.listener = listener;
    }

    public interface OutputListener {

        void onWriteToConsole(String s);

        void onReturnValue(Val v);

    }

}
