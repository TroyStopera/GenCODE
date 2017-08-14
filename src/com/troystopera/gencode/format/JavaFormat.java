package com.troystopera.gencode.format;

import com.troystopera.gencode.code.components.*;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.var.VarType;

import java.util.Map;

/**
 * Created by troy on 8/1/17.
 */
public class JavaFormat extends Format {

    protected JavaFormat() {

    }

    /* Formatting statements */

    @Override
    String formatStmtAssign(Assignment assignment) {
        return assignment.getVar() +
                " = " +
                formatEval(assignment.getEval());
    }

    @Override
    String formatStmtDeclare(Declaration declaration) {
        StringBuilder builder = new StringBuilder();

        builder.append(varTypeString(declaration.getVarType()));
        builder.append(" ");
        builder.append(declaration.getVarName());

        if (declaration.hasAssignment()) {
            builder.append(" = ");
            builder.append(formatEval(declaration.getEvaluation()));
        }

        return builder.toString();
    }

    @Override
    String formatStmtReturn(Return r) {
        return "return " + formatEval(r.getEvaluation());
    }

    /* Formatting components */

    @Override
    String formatCompConditional(Conditional conditional) {
        StringBuilder builder = new StringBuilder();
        builder.append(getIndent());

        for (Map.Entry<Comparison, CodeBlock> branch : conditional.getBranches()) {
            builder.append("if (");
            builder.append(formatEvalComparison(branch.getKey()));
            builder.append(") {\n");

            changeIndent(1);
            builder.append(formatBlock(branch.getValue()));
            changeIndent(-1);
            builder.append(getIndent());
            builder.append("} ");
        }

        if (conditional.hasElseBlock()) {
            builder.append("else {\n");
            changeIndent(1);
            builder.append(formatBlock(conditional.getElseBlock()));
            changeIndent(-1);
            builder.append(getIndent());
            builder.append("}");
        }

        return builder.toString();
    }

    @Override
    String formatCompForLoop(ForLoop forLoop) {
        StringBuilder builder = new StringBuilder();

        builder.append(getIndent());
        builder.append("for (");

        if (forLoop.hasDeclaration()) builder.append(formatStmtDeclare(forLoop.getDeclaration()));
        else builder.append(formatStmtAssign(forLoop.getAssignment()));
        builder.append("; ");

        builder.append(formatEvalComparison(forLoop.getComparison()));
        builder.append("; ");

        builder.append(formatStmtAssign(forLoop.getOpAssignment()));
        builder.append(") {\n");

        changeIndent(1);
        builder.append(formatBlock(forLoop.asCodeBlock()));
        changeIndent(-1);

        builder.append(getIndent());
        builder.append("}");

        return builder.toString();
    }

    @Override
    String formatCompWhileLoop(WhileLoop whileLoop) {
        StringBuilder builder = new StringBuilder();
        builder.append(getIndent());

        if (whileLoop.isDoWhile()) {
            builder.append("do {\n");
        } else {
            builder.append("while (");
            builder.append(formatEvalComparison(whileLoop.getComparison()));
            builder.append(") {\n");
        }

        changeIndent(1);
        builder.append(formatBlock(whileLoop.asCodeBlock()));
        changeIndent(-1);

        if (whileLoop.isDoWhile()) {
            builder.append(getIndent());
            builder.append("} while (");
            builder.append(formatEvalComparison(whileLoop.getComparison()));
            builder.append(");");
        } else {
            builder.append(getIndent());
            builder.append("}");
        }

        return builder.toString();
    }

    @Override
    String formatCompFunction(Function function) {
        StringBuilder builder = new StringBuilder();

        builder.append("public ");

        if (function.hasReturnType()) builder.append(varTypeString(function.getReturnType())).append(" ");
        else builder.append("void ");

        builder.append(function.getName());
        builder.append("(");

        Function.Argument[] arguments = function.getArgs();
        for (int i = 0; i < arguments.length; i++) {
            builder.append(varTypeString(arguments[i].type));
            builder.append(" ");
            builder.append(arguments[i].name);

            if (i < i - 1) builder.append(", ");
        }

        builder.append(") {\n");

        changeIndent(1);
        builder.append(formatBlock(function.asCodeBlock()));
        changeIndent(-1);

        builder.append("}");

        return builder.toString();
    }

    /* Formatting evaluations */

    @Override
    String formatEvalComparison(Comparison comparison) {
        return comparison.getVarName() +
                " " +
                compTypeString(comparison.getCompType()) +
                " " +
                formatEval(comparison.getEvaluation());
    }

    @Override
    String formatEvalFuncCall(FunctionCall functionCall) {
        StringBuilder builder = new StringBuilder();
        builder.append(functionCall.getFunction());
        builder.append("(");

        Evaluation[] evaluations = functionCall.getArgs();
        for (int i = 0; i < evaluations.length; i++) {
            builder.append(formatEval(evaluations[i]));

            if (i < i - 1) builder.append(", ");
        }

        builder.append(")");

        return builder.toString();
    }

    @Override
    String formatEvalOperation(Operation operation) {
        return operation.getVarName() +
                " " +
                opTypeString(operation.getOpType()) +
                " " +
                formatEval(operation.getEvaluation());
    }

    @Override
    String formatEvalValue(Value value) {
        return value.getVar().toString();
    }

    @Override
    String formatEvalVariable(Variable variable) {
        return variable.getName();
    }

    /* Static mappings */

    private static String compTypeString(ComparisonType type) {
        switch (type) {
            case GREATER_THEN:
                return ">";
            case LESS_THAN:
                return "<";
            case EQUAL_TO:
                return "==";
            case NOT_EQUAL_TO:
                return "!=";
            case GREATER_THEN_EQUAL:
                return ">=";
            case LESS_THAN_EQUAL:
                return "<=";
            default:
                throw new IllegalArgumentException("Unknown comparison '" + type + "'");
        }
    }

    private static String opTypeString(OperationType type) {
        switch (type) {
            case ADDITION:
                return "+";
            case SUBTRACTION:
                return "-";
            case MULTIPLICATION:
                return "+";
            case DIVISION:
                return "/";
            case MODULUS:
                return "%";
            default:
                throw new IllegalArgumentException("Unknown operation '" + type + "'");
        }
    }

    private static String varTypeString(VarType type) {
        switch (type) {
            case INT_ARRAY:
                return "int[]";
            case BOOLEAN_ARRAY:
                return "boolean[]";
            case STRING_ARRAY:
                return "String[]";
            case INT_PRIMITIVE:
                return "int";
            case BOOLEAN_PRIMITIVE:
                return "boolean";
            case STRING_PRIMITIVE:
                return "String";
        }
        return "*void*";
    }

}
