package com.troystopera.gencode.gen;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.ProblemTopic;
import com.troystopera.gencode.Val;
import com.troystopera.gencode.ValType;
import com.troystopera.gencode.code.BlankLine;
import com.troystopera.gencode.code.components.CodeBlock;
import com.troystopera.gencode.code.components.ForLoop;
import com.troystopera.gencode.code.components.Function;
import com.troystopera.gencode.code.statements.Assignment;
import com.troystopera.gencode.code.statements.Declaration;
import com.troystopera.gencode.code.statements.Evaluation;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.code.statements.evaluations.ComparisonType;
import com.troystopera.gencode.code.statements.evaluations.Operation;
import com.troystopera.gencode.code.statements.evaluations.OperationType;
import com.troystopera.gencode.exec.Executable;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that handles the generation of all For Loop code.
 */
public class ForLoopGenerator extends Generator {

    private static final int MAX_NEST_DEPTH = 4;
    private static final double MULT_MIN_DIF = 0.66;

    public ForLoopGenerator() {
        super(ProblemTopic.FOR_LOOPS);
    }

    @Override
    protected Problem genReturnValue(double difficulty, Problem.Builder problemBuilder) {
        Function main = new Function("main", ValType.INT);
        main.addExecutables(genForReturnValue(difficulty));

        problemBuilder.setMainFunction(main);
        return problemBuilder.build();
    }

    private List<Executable> genForReturnValue(double difficulty) {
        LinkedList<Executable> executables = new LinkedList<>();

        //determine how deep to nest the loops
        int nestDepth = randomInt(MAX_NEST_DEPTH, difficulty);
        if (nestDepth <= 0) nestDepth = 1;

        //create the var that is manipulated and declare it
        String manipulatedVar = "var";
        executables.add(Declaration.declareWithAssign(manipulatedVar, ValType.INT, Val.intVal(0)));

        //keep track of the last loop generated
        CodeBlock lastLoop = new CodeBlock();
        //add this instance of lastLoop to the list
        executables.add(lastLoop);

        for (int i = 0; i < nestDepth; i++) {
            //generate 1 or 2 variables outside the loop
            String[] vars = newVarNames(randomInt(1, 3));
            //set the variables
            lastLoop.addExecutables(VariableSetter.declareInts(difficulty, vars));
            lastLoop.addExecutable(BlankLine.get());

            //based on difficulty randomly decide if a new variable should be used for the for loop
            ForLoop loop = randomBool(difficulty) ? getForLoop(difficulty, vars, null) :
                    getForLoop(difficulty, vars, newVarName());

            //determine how to manipulate the var
            Operation manipulationOp;
            if (randomBool()) manipulationOp = Operation.addition(manipulatedVar, Val.intVal(1));
            else manipulationOp = Operation.subtraction(manipulatedVar, Val.intVal(1));
            loop.addExecutable(Assignment.assign(manipulatedVar, manipulationOp));

            lastLoop.addExecutable(loop);
            lastLoop = loop;
        }

        executables.add(BlankLine.get());
        executables.add(Return.returnStmt(manipulatedVar));

        return executables;
    }

    @Override
    protected Problem genPrintOut(double difficulty, Problem.Builder problemBuilder) {
        return genReturnValue(difficulty, problemBuilder);
    }

    @Override
    protected Problem genWriteCode(double difficulty, Problem.Builder problemBuilder) {
        return genReturnValue(difficulty, problemBuilder);
    }

    private ForLoop getForLoop(double difficulty, String[] vars, String forVar) {
        String var = forVar != null ? forVar : vars[random.nextInt(vars.length)];
        String compVar = randomBool(difficulty) ? vars[random.nextInt(vars.length)] : null;

        Comparison comparison = getRandomComp(var, compVar);
        Assignment assignment = getRandomAssignment(difficulty, comparison, vars);
        OperationType opType = ((Operation) assignment.getEval()).getOpType();

        if (forVar == null) {
            Assignment loopAssign;
            if (opType == OperationType.ADDITION) loopAssign = Assignment.assign(var, Val.intVal(0));
            else if (opType == OperationType.MULTIPLICATION) loopAssign = Assignment.assign(var, Val.intVal(1));
            else loopAssign = Assignment.assign(var, Val.intVal(50 + randomInt(0, 50)));

            return new ForLoop(loopAssign, comparison, assignment);
        } else {
            Declaration declaration;
            if (opType == OperationType.ADDITION)
                declaration = Declaration.declareWithAssign(var, ValType.INT, Val.intVal(0));
            else if (opType == OperationType.MULTIPLICATION)
                declaration = Declaration.declareWithAssign(var, ValType.INT, Val.intVal(1));
            else declaration = Declaration.declareWithAssign(var, ValType.INT, Val.intVal(50 + randomInt(0, 50)));

            return new ForLoop(declaration, comparison, assignment);
        }
    }

    private Assignment getRandomAssignment(double difficulty, Comparison comparison, String[] vars) {
        Evaluation evaluation;
        if (comparison.getCompType() == ComparisonType.LESS_THAN || comparison.getCompType() == ComparisonType.LESS_THAN_EQUAL) {
            //use multiplication more the higher the difficulty is
            if (difficulty >= MULT_MIN_DIF && randomBool(difficulty)) {
                evaluation = Operation.multiplication(comparison.getVar(), Val.intVal(randomInt(1, 3)));
            }
            //if not multiplication, use addition of vars more the higher the difficulty is
            else if (randomBool(difficulty)) {
                evaluation = Operation.addition(comparison.getVar(), vars[random.nextInt(vars.length)]);
            }
            //otherwise use addition with a value
            else evaluation = Operation.addition(comparison.getVar(), Val.intVal(randomInt(1, 3)));
        } else {
            //based on difficulty, add the value of a var
            if (randomBool(difficulty))
                evaluation = Operation.subtraction(comparison.getVar(), vars[random.nextInt(vars.length)]);
            else evaluation = Operation.subtraction(comparison.getVar(), Val.intVal(randomInt(1, 3)));
        }
        return Assignment.assign(comparison.getVar(), evaluation);
    }

    //generate a random ComparisonType
    private Comparison getRandomComp(String forVar, String compVal) {
        switch (random.nextInt(4)) {
            case 0:
                return compVal != null ? Comparison.greaterThan(forVar, compVal) :
                        Comparison.greaterThan(forVar, Val.intVal(randomInt(0, 50)));
            case 1:
                return compVal != null ? Comparison.greaterThanEqual(forVar, compVal) :
                        Comparison.greaterThanEqual(forVar, Val.intVal(randomInt(0, 50)));
            case 2:
                return compVal != null ? Comparison.lessThan(forVar, compVal) :
                        Comparison.lessThan(forVar, Val.intVal(50 + randomInt(0, 50)));
            case 3:
                return compVal != null ? Comparison.lessThanEqual(forVar, compVal) :
                        Comparison.lessThanEqual(forVar, Val.intVal(50 + randomInt(0, 50)));
            default:
                return compVal != null ? Comparison.lessThan(forVar, compVal) :
                        Comparison.lessThan(forVar, Val.intVal(50 + randomInt(0, 50)));
        }
    }

}
