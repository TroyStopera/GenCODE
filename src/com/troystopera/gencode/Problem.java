package com.troystopera.gencode;

import com.troystopera.gencode.code.components.Function;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/***
 * Represents a generated problem. Used to pass generated code in a neat fashion.
 */
public class Problem {

    private final Function mainFunction;
    private final List<Function> functions = new LinkedList<>();
    private final ProblemTopic topic;
    private final ProblemType type;

    public Problem(Function mainFunction, Collection<Function> functions, ProblemTopic topic, ProblemType type) {
        this.mainFunction = mainFunction;
        this.functions.addAll(functions);
        this.topic = topic;
        this.type = type;
    }

    public Function getMainFunction() {
        return mainFunction;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public static class Builder {

        private Function mainFunction;
        private final Collection<Function> functions = new LinkedList<>();
        private ProblemTopic topic;
        private ProblemType type;

        public void setMainFunction(Function mainFunction) {
            this.mainFunction = mainFunction;
        }

        public void addFunctions(Collection<Function> executables) {
            this.functions.addAll(executables);
        }

        public void addFunction(Function executable) {
            this.functions.add(executable);
        }

        public void setTopic(ProblemTopic topic) {
            this.topic = topic;
        }

        public void setType(ProblemType type) {
            this.type = type;
        }

        public Problem build() {
            functions.add(mainFunction);
            return new Problem(mainFunction, functions, topic, type);
        }

    }

}
