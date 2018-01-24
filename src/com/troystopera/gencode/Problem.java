package com.troystopera.gencode;

import com.troystopera.jkode.JFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/***
 * Represents a generated problem. Used to pass generated code in a neat fashion.
 */
public class Problem implements Comparable<Problem> {

    private final JFunction mainFunction;
    private final List<JFunction> functions = new LinkedList<>();
    private final ProblemTopic[] topics;
    private final ProblemType type;
    private final double difficulty;

    public Problem(JFunction mainFunction, Collection<JFunction> functions, ProblemTopic[] topics, ProblemType type, double difficulty) {
        this.mainFunction = mainFunction;
        this.functions.addAll(functions);
        this.topics = topics;
        this.type = type;
        this.difficulty = difficulty;
    }

    public JFunction getMainFunction() {
        return mainFunction;
    }

    public List<JFunction> getFunctions() {
        return functions;
    }

    @Override
    public int compareTo(@NotNull Problem o) {
        return Double.compare(difficulty, o.difficulty);
    }

    public static class Builder {

        private JFunction mainFunction;
        private final Collection<JFunction> functions = new LinkedList<>();
        private ProblemTopic[] topics;
        private ProblemType type;
        private double difficulty;

        public void setMainFunction(JFunction mainFunction) {
            this.mainFunction = mainFunction;
        }

        public void addAuxFunctions(Collection<JFunction> executables) {
            this.functions.addAll(executables);
        }

        public void addAuxFunction(JFunction executable) {
            this.functions.add(executable);
        }

        public void setTopics(ProblemTopic... topics) {
            this.topics = topics;
        }

        public void setType(ProblemType type) {
            this.type = type;
        }

        public void setDifficulty(double difficulty) {
            this.difficulty = difficulty;
        }

        public Problem build() {
            functions.add(mainFunction);
            return new Problem(mainFunction, functions, topics, type, difficulty);
        }

    }

}
