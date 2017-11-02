package com.troystopera.gencode.format;

import com.troystopera.gencode.code.components.*;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.var.*;

import java.util.Map;

public class JavaFormat extends Format {

    protected JavaFormat() {

    }

    /* Formatting statements */

    @Override
    String formatStmtAssign(Assignment assignment) {
        String var;
        if (assignment.isArrayIndexAssign()) {
            var = assignment.getVar() + "[" + assignment.getArrayIndex() + "]";
        } else var = assignment.getVar();

        return var + " = " + formatEval(assignment.getEval());
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
        return formatEval(comparison.getLeft()) +
                " " +
                compTypeString(comparison.getCompType()) +
                " " +
                formatEval(comparison.getRight());
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
    String formatEvalOperation(MathOperation mathOperation) {
        return mathOperation.getVarName() +
                " " +
                opTypeString(mathOperation.getOpType()) +
                " " +
                formatEval(mathOperation.getEvaluation());
    }

    @SuppressWarnings("unchecked")
    @Override
    String formatEvalValue(Value value) {
        switch (value.getVar().getType()) {
            case INT_ARRAY:
                ArrayVar<IntVar> ints = (ArrayVar<IntVar>) value.getVar();
                StringBuilder intArr = new StringBuilder().append("{");
                for (IntVar intVar : ints.getArray())
                    intArr.append(formatEvalValue(Value.of(intVar))).append(", ");
                //remove last comma and space if present
                if (intArr.length() > 1)
                    intArr.setLength(intArr.length() - 2);
                intArr.append("}");
                return intArr.toString();
            case BOOLEAN_ARRAY:
                ArrayVar<BooleanVar> bools = (ArrayVar<BooleanVar>) value.getVar();
                StringBuilder boolArr = new StringBuilder().append("{");
                for (BooleanVar boolVar : bools.getArray())
                    boolArr.append(formatEvalValue(Value.of(boolVar))).append(", ");
                //remove last comma and space if present
                if (boolArr.length() > 1)
                    boolArr.setLength(boolArr.length() - 2);
                boolArr.append("}");
                return boolArr.toString();
            case STRING_ARRAY:
                ArrayVar<StringVar> strings = (ArrayVar<StringVar>) value.getVar();
                StringBuilder stringArr = new StringBuilder().append("{");
                for (StringVar stringVar : strings.getArray())
                    stringArr.append(formatEvalValue(Value.of(stringVar))).append(", ");
                //remove last comma and space if present
                if (stringArr.length() > 1)
                    stringArr.setLength(stringArr.length() - 2);
                stringArr.append("}");
                return stringArr.toString();
            case INT_PRIMITIVE:
                return String.valueOf(((IntVar) value.getVar()).getValue());
            case BOOLEAN_PRIMITIVE:
                return String.valueOf(((BooleanVar) value.getVar()).getValue()).toLowerCase();
            case STRING_PRIMITIVE:
                return "\"" + ((StringVar) value.getVar()).getValue() + "\"";

        }
        return value.getVar().toString();
    }

    @Override
    String formatEvalVariable(Variable variable) {
        return variable.getName();
    }

    @Override
    String formatEvalArrayAccess(ArrayAccess arrayAccess) {
        return arrayAccess.getArray() + "[" + arrayAccess.getIndex() + "]";
    }

    @Override
    String formatEvalArrayLength(ArrayLength arrayLength) {
        return arrayLength.getArray() + ".length";
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
                return "*";
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
