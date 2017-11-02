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
    private final ProblemTopic[] topics;
    private final ProblemType type;

    public Problem(Function mainFunction, Collection<Function> functions, ProblemTopic[] topics, ProblemType type) {
        this.mainFunction = mainFunction;
        this.functions.addAll(functions);
        this.topics = topics;
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
        private ProblemTopic[] topics;
        private ProblemType type;

        public void setMainFunction(Function mainFunction) {
            this.mainFunction = mainFunction;
        }

        public void addAuxFunctions(Collection<Function> executables) {
            this.functions.addAll(executables);
        }

        public void addAuxFunction(Function executable) {
            this.functions.add(executable);
        }

        public void setTopics(ProblemTopic... topics) {
            this.topics = topics;
        }

        public void setType(ProblemType type) {
            this.type = type;
        }

        public Problem build() {
            functions.add(mainFunction);
            return new Problem(mainFunction, functions, topics, type);
        }

    }

}
