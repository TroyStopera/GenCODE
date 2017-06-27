package com.troystopera.gencode.format;

import com.troystopera.gencode.val.Val;
import com.troystopera.gencode.val.ValType;
import com.troystopera.gencode.code.Component;
import com.troystopera.gencode.code.Statement;
import com.troystopera.gencode.code.components.*;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.*;
import com.troystopera.gencode.exec.Executable;

import java.util.Map;

public class JavaFormat extends Format.Formatter {

    private int indent = 0;

    @Override
    void formatStatement(Statement statement, StringBuilder builder) {
        applyIndent(builder);
        //now translate the statement
        switch (statement.getType()) {
            case ASSIGNMENT:
                Assignment assignment = (Assignment) statement;
                builder.append(assignment.getVar());
                Evaluation evaluation = assignment.getEval();
                //check for easier-to-read for simple incrementing
                if (evaluation.getEvalType() == Evaluation.Type.OPERATION) {
                    Operation operation = (Operation) evaluation;
                    //make sure it is by value and it is an int
                    if (operation.getEvaluation().getEvalType() == Evaluation.Type.VALUE) {
                        Val val = ((Value) operation.getEvaluation()).getVal();
                        //make sure the value is an int
                        if (val.type == ValType.INT) {
                            int iVal = val.asInt();
                            //handle increment vs decrement
                            if (operation.getOpType() == OperationType.ADDITION) {
                                if (iVal == 1) builder.append("++");
                                else builder.append(" += ").append(iVal);
                                //don't continue formatting
                                break;
                            } else if (operation.getOpType() == OperationType.SUBTRACTION) {
                                if (iVal == 1) builder.append("--");
                                else builder.append(" -= ").append(iVal);
                                //don't continue formatting
                                break;
                            }
                        }
                    }
                }
                //otherwise print the long form
                builder.append(" = ");
                formatEvaluation(evaluation, builder, false);
                break;
            case DECLARATION:
                Declaration declaration = (Declaration) statement;
                builder.append(valTypeToString(declaration.getValType())).append(" ").append(declaration.getVarName());
                if (declaration.hasAssignment()) {
                    builder.append(" = ");
                    formatEvaluation(declaration.getEvaluation(), builder, false);
                }
                break;
            case RETURN:
                Return ret = (Return) statement;
                builder.append("return ");
                //append the proper return type
                formatEvaluation(ret.getEvaluation(), builder, false);
                break;
            case EVALUATION:
                formatEvaluation((Evaluation) statement, builder, true);
                break;
        }
        //add the semicolon and linebreak after all statements in Java
        builder.append(";\n");
    }

    @Override
    void formatEvaluation(Evaluation evaluation, StringBuilder builder, boolean indent) {
        if (indent) applyIndent(builder);
        //now translate the evaluation
        switch (evaluation.getEvalType()) {
            case COMPARISON:
                Comparison comparison = (Comparison) evaluation;
                builder.append(comparison.getVar())
                        .append(" ").append(compToString(comparison.getCompType())).append(" ");
                formatEvaluation(comparison.getEvaluation(), builder, false);
                break;
            case FUNC_CALL:
                FunctionCall functionCall = (FunctionCall) evaluation;
                builder.append(functionCall.getFunction()).append("(");
                //include all arguments
                Evaluation[] evaluations = functionCall.getArgs();
                for (int i = 0; i < evaluations.length - 1; i++) {
                    formatEvaluation(evaluations[i], builder, false);
                    builder.append(", ");
                }
                //include last argument and close paren
                formatEvaluation(evaluations[evaluations.length - 1], builder, false);
                builder.append(")");
                break;
            case OPERATION:
                Operation operation = (Operation) evaluation;
                builder.append(operation.getVar())
                        .append(" ").append(opToString(operation.getOpType())).append(" ");
                formatEvaluation(operation.getEvaluation(), builder, false);
                break;
            case VALUE:
                Value value = (Value) evaluation;
                builder.append(valToString(value.getVal()));
                break;
            case VARIABLE:
                Variable variable = (Variable) evaluation;
                builder.append(variable.getName());
                break;
        }

    }

    @Override
    void formatComponent(Component component, StringBuilder builder) {
        int tempIndent;
        //translate the component
        switch (component.getType()) {
            case GENERIC:
                CodeBlock block = (CodeBlock) component;
                for (Executable executable : block.getExecutables())
                    format(executable, builder);
                break;
            case CONDITIONAL:
                Conditional conditional = (Conditional) component;
                boolean elseIf = false;
                //handle the if/else-if branches
                for (Map.Entry<Comparison, CodeBlock> branch : conditional.getBranches()) {
                    //otherwise add an indent
                    if (!elseIf) applyIndent(builder);
                    //start the branch
                    builder.append(elseIf ? " else if (" : "if (");
                    //add the comparison, but without indent and then delete the newline and semicolon
                    formatEvaluation(branch.getKey(), builder, false);
                    //close the line of the if branch
                    builder.append(") {\n");
                    //apply the code block with new indent
                    indent++;
                    formatComponent(branch.getValue(), builder);
                    indent--;
                    //close the branch
                    applyIndent(builder);
                    builder.append("}");

                    if (!elseIf) elseIf = true;
                }
                //handle the else branch if it exists
                if (conditional.hasElseBlock()) {
                    //start the if branch
                    builder.append(" else {\n");
                    //apply the code block with new indent
                    indent++;
                    formatComponent(conditional.getElseBlock(), builder);
                    indent--;
                    //close the branch
                    applyIndent(builder);
                    builder.append("}");
                }
                builder.append("\n");
                break;
            case FOR_LOOP:
                ForLoop forLoop = (ForLoop) component;
                applyIndent(builder);
                builder.append("for (");
                //temporarily remove indent
                tempIndent = indent;
                indent = 0;
                //apply the first loop part then replace newline with space
                if (forLoop.hasDeclaration()) formatStatement(forLoop.getDeclaration(), builder);
                else formatStatement(forLoop.getAssignment(), builder);
                builder.setLength(builder.length() - 1);
                builder.append(" ");
                //apply the comparison then replace newline with space
                formatEvaluation(forLoop.getComparison(), builder, false);
                builder.append("; ");
                //apply the operation and remove both semicolon and newline
                formatStatement(forLoop.getOpAssignment(), builder);
                builder.setLength(builder.length() - 2);
                builder.append(") {\n");
                //reapply the indent
                indent = tempIndent;
                //add the code block
                indent++;
                formatComponent(forLoop.asCodeBlock(), builder);
                indent--;
                applyIndent(builder);
                builder.append("}\n");
                break;
            case WHILE_LOOP:
                WhileLoop whileLoop = (WhileLoop) component;
                applyIndent(builder);
                if (whileLoop.isDoWhile()) {
                    builder.append("do {\n");
                    //add the block
                    indent++;
                    formatComponent(whileLoop.asCodeBlock(), builder);
                    indent--;
                    //add the while portion
                    applyIndent(builder);
                    builder.append("} while (");
                    //add the comparison with no indent and then remove newline and semicolon
                    formatEvaluation(whileLoop.getComparison(), builder, false);
                    builder.append(");\n");
                } else {
                    builder.append("while (");
                    formatEvaluation(whileLoop.getComparison(), builder, false);
                    builder.append(") {\n");
                    //add the code block
                    indent++;
                    formatComponent(whileLoop.asCodeBlock(), builder);
                    indent--;
                    //close the loop
                    applyIndent(builder);
                    builder.append("}\n");
                }
                break;
            case FUNCTION:
                Function function = (Function) component;
                applyIndent(builder);
                //add the method signature
                builder.append(function.hasReturnType() ? valTypeToString(function.getReturnType()) : "void").append(" ")
                        .append(function.getName()).append("(");
                //add the arguments
                for (Function.Argument argument : function.getArgs())
                    builder.append(valTypeToString(argument.type))
                            .append(" ").append(argument.name);
                //end the signature
                builder.append(") {\n");
                //add the code within the function
                indent++;
                formatComponent(function.asCodeBlock(), builder);
                indent--;
                //close the function
                applyIndent(builder);
                builder.append("}\n");
                break;
        }
    }

    private void applyIndent(StringBuilder builder) {
        //add proper number of indents
        for (int i = 0; i < indent; i++) builder.append("    ");
    }

    /*
        Below are simple helper functions for re-used symbols and words.
     */

    private static String opToString(OperationType type) {
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
        }
        return "";
    }

    private static String compToString(ComparisonType type) {
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
        }
        return "";
    }

    private static String valToString(Val val) {
        switch (val.type) {
            case INT:
                return val.toString();
            case STRING:
                return "\"" + val.asString() + "\"";
            case BOOLEAN:
                return val.toString();
        }
        return val.toString();
    }

    private static String valTypeToString(ValType type) {
        switch (type) {
            case INT:
                return "int";
            case STRING:
                return "String";
            case BOOLEAN:
                return "boolean";
        }
        return "";
    }

}
