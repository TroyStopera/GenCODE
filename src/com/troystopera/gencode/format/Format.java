package com.troystopera.gencode.format;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.code.Component;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.components.*;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.exec.Executable;

/**
 * Created by troy on 8/1/17.
 */
public abstract class Format {

    private static JavaFormat javaFormat;

    private int indentation = 0;

    public final String format(Problem problem) {
        indentation = 0;
        StringBuilder builder = new StringBuilder();
        for (Function function : problem.getFunctions())
            builder.append(formatCompFunction(function)).append("\n\n");
        return builder.toString();
    }

    final String formatBlock(CodeBlock codeBlock) {
        StringBuilder builder = new StringBuilder();
        for (Executable executable : codeBlock.getExecutables()) {
            switch (executable.getExecType()) {
                case STATEMENT:
                    builder.append(formatStmt((Statement) executable)).append('\n');
                    break;
                case COMPONENT:
                    builder.append(formatComp((Component) executable)).append('\n');
                    break;
                case EMPTY:
                    builder.append('\n');
                    break;
            }
        }
        return builder.toString();
    }

    final String formatEval(Evaluation evaluation) {
        switch (evaluation.getEvalType()) {
            case COMPARISON:
                return formatEvalComparison((Comparison) evaluation);
            case FUNC_CALL:
                return formatEvalFuncCall((FunctionCall) evaluation);
            case OPERATION:
                return formatEvalOperation((MathOperation) evaluation);
            case VALUE:
                return formatEvalValue((Value) evaluation);
            case VARIABLE:
                return formatEvalVariable((Variable) evaluation);
            case ARRAY_ACCESS:
                return formatEvalArrayAccess((ArrayAccess) evaluation);
            case ARRAY_LENGTH:
                return formatEvalArrayLength((ArrayLength) evaluation);
        }
        return "";
    }

    private String formatStmt(Statement statement) {
        switch (statement.getType()) {
            case ASSIGNMENT:
                return getIndent() + formatStmtAssign((Assignment) statement) + ";";
            case DECLARATION:
                return getIndent() + formatStmtDeclare((Declaration) statement) + ";";
            case RETURN:
                return getIndent() + formatStmtReturn((Return) statement) + ";";
            case EVALUATION:
                return getIndent() + formatEval((Evaluation) statement) + ";";
        }
        return "";
    }

    private String formatComp(Component component) {
        switch (component.getType()) {
            case GENERIC:
                return formatBlock((CodeBlock) component) + '\n';
            case CONDITIONAL:
                return formatCompConditional((Conditional) component) + '\n';
            case FOR_LOOP:
                return formatCompForLoop((ForLoop) component) + '\n';
            case WHILE_LOOP:
                return formatCompWhileLoop((WhileLoop) component) + '\n';
            case FUNCTION:
                return formatCompFunction((Function) component) + '\n';
        }
        return "";
    }

    /* Formatting statements */

    abstract String formatStmtAssign(Assignment assignment);

    abstract String formatStmtDeclare(Declaration declaration);

    abstract String formatStmtReturn(Return r);

    /* Formatting components */

    abstract String formatCompConditional(Conditional conditional);

    abstract String formatCompForLoop(ForLoop forLoop);

    abstract String formatCompWhileLoop(WhileLoop whileLoop);

    abstract String formatCompFunction(Function function);

    /* Formatting evaluations */

    abstract String formatEvalComparison(Comparison comparison);

    abstract String formatEvalFuncCall(FunctionCall functionCall);

    abstract String formatEvalOperation(MathOperation mathOperation);

    abstract String formatEvalValue(Value value);

    abstract String formatEvalVariable(Variable variable);

    abstract String formatEvalArrayAccess(ArrayAccess arrayAccess);

    abstract String formatEvalArrayLength(ArrayLength arrayLength);

    /* Indentation controls */

    void changeIndent(int change) {
        indentation += change;
    }

    String getIndent() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indentation; i++)
            builder.append("    ");
        return builder.toString();
    }

    /* Getters for formats */

    public static JavaFormat java() {
        if (javaFormat == null)
            javaFormat = new JavaFormat();
        return javaFormat;
    }

}
