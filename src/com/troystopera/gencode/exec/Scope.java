package com.troystopera.gencode.exec;

import com.troystopera.gencode.GenerationException;
import com.troystopera.gencode.code.components.Function;
import com.troystopera.gencode.var.ArrayVar;
import com.troystopera.gencode.var.Var;

import java.util.*;

/***
 * Representation of a scope. It maintains a list of all variables declared within it, as well as the values of
 * variables for use when executing the code to generate answers to questions.
 */
public class Scope {

    private static final Random random = new Random();

    //TODO: keep track of var types?
    private final Map<String, Var> vars = new HashMap<>();
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

    public void addVar(String name) {
        addVar(name, null);
    }

    public void addVar(String name, Var var) {
        varNames.add(name);
        vars.put(name, var);
    }

    public void nullVar(String var) {
        vars.remove(var);
    }

    public void nullArrVar(String var, int index) {
        ArrayVar array = (ArrayVar) getVal(var);
        if (array.getArray().length <= index)
            throw new GenerationException(new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds"));
        array.getArray()[index] = null;
    }

    public Var getVal(String name) {
        Scope scope = this;
        while (!scope.vars.containsKey(name) && scope.hasParent()) scope = scope.getParent();
        if (scope.vars.containsKey(name)) return scope.vars.get(name);
        throw new GenerationException(new NullPointerException("Unknown variable: " + name));
    }

    public void updateVal(String name, Var var) {
        Scope scope = this;
        while (!scope.varNames.contains(name) && scope.hasParent()) scope = scope.getParent();
        if (scope.varNames.contains(name)) scope.vars.put(name, var);
        else throw new GenerationException(new NullPointerException("Unknown variable: " + name));
    }

    @SuppressWarnings("unchecked")
    public Var getArrVal(String name, int index) {
        ArrayVar<Var> array = (ArrayVar<Var>) getVal(name);
        if (array.getArray().length <= index)
            throw new GenerationException(new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds"));
        return array.getArray()[index];
    }

    @SuppressWarnings("unchecked")
    public <T extends Var> void updateArrVal(String name, int index, T var) {
        ArrayVar<T> array = (ArrayVar<T>) getVal(name);
        if (array.getArray().length <= index)
            throw new GenerationException(new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds"));
        array.getArray()[index] = var;
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

        void onReturnValue(Var v);

    }

}
