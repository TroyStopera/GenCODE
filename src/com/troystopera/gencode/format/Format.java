package com.troystopera.gencode.format;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.code.Component;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.exec.Executable;

/***
 * This class provides a system to format generated code to specific languages.
 */
public final class Format {

    public static Formatter java() {
        return new JavaFormat();
    }

    /*
        Abstract class that can be extended to format to any language.
     */
    public static abstract class Formatter {

        public final String format(Problem problem) {
            StringBuilder builder = new StringBuilder();
            for (Executable executable : problem.getFunctions())
                format(executable, builder);
            return builder.toString();
        }

        void format(Executable executable, StringBuilder builder) {
            switch (executable.getExecType()) {
                case STATEMENT:
                    Statement statement = (Statement) executable;
                    if (statement.getType() == Statement.Type.EVALUATION)
                        formatEvaluation((Evaluation) statement, builder, true);
                    else formatStatement(statement, builder);
                    break;
                case COMPONENT:
                    formatComponent((Component) executable, builder);
                    break;
                case EMPTY:
                    builder.append('\n');
                    break;
            }
        }

        abstract void formatStatement(Statement statement, StringBuilder builder);

        abstract void formatEvaluation(Evaluation evaluation, StringBuilder builder, boolean indent);

        abstract void formatComponent(Component component, StringBuilder builder);

    }

}
