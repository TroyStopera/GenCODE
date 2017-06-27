package com.troystopera.gencode.gen;

import com.troystopera.gencode.Problem;
import com.troystopera.gencode.ProblemTopic;
import com.troystopera.gencode.Val;
import com.troystopera.gencode.ValType;
import com.troystopera.gencode.code.BlankLine;
import com.troystopera.gencode.code.components.CodeBlock;
import com.troystopera.gencode.code.components.Conditional;
import com.troystopera.gencode.code.components.Function;
import com.troystopera.gencode.code.statements.Return;
import com.troystopera.gencode.code.statements.evaluations.Comparison;
import com.troystopera.gencode.code.statements.evaluations.ComparisonType;
import com.troystopera.gencode.exec.Executable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that handles the generation of all Conditional code.
 */
public class ConditionalGenerator extends Generator {

    private static final int MAX_BRANCHES = 10;
    private static final int MAX_VARS = 5;
    //TODO: implement more nesting
    private static final int MAX_NEST_DEPTH = 3;

    private static final double NESTED_MIN_DIFFICULTY = .4;
    private static final double MULTIPLE_BLOCKS_MIN_DIFFICULTY = .33;

    public ConditionalGenerator() {
        super(ProblemTopic.CONDITIONALS);
    }

    @Override
    protected Problem genReturnValue(double difficulty, Problem.Builder problemBuilder) {
        Function main = new Function("main", ValType.INT);
        main.addExecutables(genCodeReturnValue(difficulty));

        problemBuilder.setMainFunction(main);

        return problemBuilder.build();
    }

    @Override
    protected Problem genPrintOut(double difficulty, Problem.Builder problemBuilder) {
        //TODO: implement System.out.println() questions
        return genReturnValue(difficulty, problemBuilder);
    }

    @Override
    protected Problem genWriteCode(double difficulty, Problem.Builder problemBuilder) {
        //TODO: implement write code questions
        return genReturnValue(difficulty, problemBuilder);
    }

    private List<Executable> genCodeReturnValue(double difficulty) {
        List<Executable> executables = new LinkedList<>();

        //generate array of initial variables
        int varCount = randomInt(MAX_VARS, difficulty);
        String[] vars = newVarNames(varCount == 0 ? 1 : varCount);
        //set the variables
        executables.add(VariableSetter.declareInts(difficulty, vars));
        executables.add(BlankLine.get());

        //determine a number of branches (at least 2)
        int branchCount = randomInt(MAX_BRANCHES, difficulty);
        if (branchCount < 2) branchCount = 2;

        Conditional conditional = new Conditional();
        Comparison[] comparisons = genIntComps(branchCount, vars);

        //determine if this conditional should have an else branch
        boolean elseBlock = randomBool();
        //if using an else block, generate 1 less if/else
        if (elseBlock) branchCount--;

        //create the proper number of branches
        for (int i = 0; i < branchCount; i++) {
            int index = i;
            //start with adding the branch, then add the logic to the block
            CodeBlock condBlock = new CodeBlock();

            int remainingBranches = branchCount - i - 1;

            //if this is not the last conditional && a weighted coin flip add nested
            if (remainingBranches >= 2 && difficulty >= NESTED_MIN_DIFFICULTY && randomBool(difficulty)) {
                //if there needs to be an else block, don't allow the sub conditionals to take up all of the remaining slots
                int branches = randomInt(2, elseBlock ? remainingBranches - 1 : remainingBranches);
                //create sub array
                Comparison[] subComps = Arrays.copyOfRange(comparisons, i + 1, i + branches);

                //if the outer conditional has an else block, this sub conditional must return something
                boolean includeElse = branches > 1 && elseBlock && randomBool();

                //add generated sub conditionals
                condBlock.addExecutable(genSubConditionalReturn(subComps, includeElse));

                //add a simple return value if necessary
                if (elseBlock && !includeElse)
                    condBlock.addExecutable(Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX)));

                //update i so that extra branches aren't created and update remainingBranches
                i += branches;
                remainingBranches -= branches;
            } else condBlock.addExecutable(Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX)));

            conditional.addBranch(comparisons[index], condBlock);

            //if the difficulty is high enough and with a 33% chance create a new Conditional
            if (remainingBranches > 1 && difficulty > MULTIPLE_BLOCKS_MIN_DIFFICULTY && random.nextDouble() < 0.33) {
                executables.add(conditional);
                executables.add(BlankLine.get());
                conditional = new Conditional();
            }

        }

        executables.add(conditional);
        //if using else add it, otherwise add a return
        if (elseBlock) conditional.setElse(new CodeBlock(
                Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX)))
        );
        else {
            executables.add(BlankLine.get());
            executables.add(Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX)));
        }

        return executables;
    }

    private Conditional genSubConditionalReturn(Comparison[] comparisons, boolean includeElse) {
        Conditional innerConditional = new Conditional();

        for (Comparison comparison : comparisons) {
            CodeBlock block = new CodeBlock();
            block.addExecutable(Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX)));
            innerConditional.addBranch(comparison, block);
        }

        if (includeElse) {
            innerConditional.setElse(new CodeBlock(Return.returnStmt(Val.Int.random(RANDOM_RETURN_MAX))));
        }

        return innerConditional;
    }

    private Comparison[] genIntComps(int count, String[] vars) {
        Comparison[] comparisons = new Comparison[count];

        for (int i = 0; i < comparisons.length; i++) {
            Comparison comparison;
            //determine which vars to compare and generate a Val in case that is used instead
            String var1 = vars[randomInt(0, vars.length)];
            String var2 = vars[randomInt(0, vars.length)];
            Val intVal = Val.Int.random(100);

            //generate the comparison
            switch (ComparisonType.randomType()) {
                case GREATER_THEN:
                    if (randomBool()) comparison = Comparison.greaterThan(var1, var2);
                    else comparison = Comparison.greaterThan(var1, intVal);
                    break;
                case LESS_THAN:
                    if (randomBool()) comparison = Comparison.lessThan(var1, var2);
                    else comparison = Comparison.lessThan(var1, intVal);
                    break;
                case EQUAL_TO:
                    if (randomBool()) comparison = Comparison.equalTo(var1, var2);
                    else comparison = Comparison.equalTo(var1, intVal);
                    break;
                case NOT_EQUAL_TO:
                    if (randomBool()) comparison = Comparison.notEqualTo(var1, var2);
                    else comparison = Comparison.notEqualTo(var1, intVal);
                    break;
                case GREATER_THEN_EQUAL:
                    if (randomBool()) comparison = Comparison.greaterThanEqual(var1, var2);
                    else comparison = Comparison.greaterThanEqual(var1, intVal);
                    break;
                case LESS_THAN_EQUAL:
                    if (randomBool()) comparison = Comparison.lessThanEqual(var1, var2);
                    else comparison = Comparison.lessThanEqual(var1, intVal);
                    break;
                default:
                    if (randomBool()) comparison = Comparison.equalTo(var1, var2);
                    else comparison = Comparison.equalTo(var1, intVal);
                    break;
            }

            comparisons[i] = comparison;
        }

        return comparisons;
    }

}
