package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.gencode.generator.constraints.ManipulationConstraints
import com.troystopera.jkode.Component
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.ArrayAccess
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.statements.ArrayAssign
import com.troystopera.jkode.statements.Assignment
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal class ManipulationProvider(
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.MANIPULATION, random, topics) {

    override fun populate(parent: CodeBlock, varProvider: VarNameProvider, scope: GenScope, context: GenContext) {
        if (!scope.hasVarType(VarType.INT))
            throw GenerationException(IllegalStateException("No ints in scope passed to ManipulationProvider"))

        var count = 0
        val default = {
            if (scope.isIn(ForLoop::class))
                MathOperation(
                        MathOperation.Type.ADD,
                        Variable(VarType.INT, scope.getRandVar(VarType.INT)!!),
                        Variable(VarType.INT, scope.getRandUnmanipVar(VarType.INT)!!))
            else easyIntEval.invoke()
        }

        //start by checking for an array generation pattern
        if (scope.hasPattern(Pattern.ArrayWalk::class)) {
            val arrayWalk = scope.getPattern(Pattern.ArrayWalk::class)!! as Pattern.ArrayWalk
            if (ManipulationConstraints.useDirectManipulation(random)) {
                parent.add(ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arrayWalk.arrayName),
                        Variable(VarType.INT, arrayWalk.index),
                        Variable(VarType.INT, arrayWalk.index)
                ))
                count++
            } else {
                //manipulate an int that may be used by the array assign
                val assign = forLoopManip(context, scope)
                parent.add(assign)
                parent.add(ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arrayWalk.arrayName),
                        Variable(VarType.INT, arrayWalk.index),
                        Variable(VarType.INT, assign.varName)
                ))
                count += 2
            }
        }

        //manipulate the return var if present and not in an array walk
        if (!scope.hasPattern(Pattern.ArrayWalk::class) && context.mainIntVar != null) {
            if (scope.isIn(ForLoop::class))
                parent.add(forLoopManip(context, scope))
            else
                parent.add(Assignment(context.mainIntVar!!, genIntEvaluation(scope, context.mainIntVar!!)))
            count++
        }

        while (count < MIN_OPERATIONS || (count < MAX_OPERATIONS && random.randHardBool())) {
            val manipulateVar = scope.getRandVar(VarType.INT)!!
            //potentially manipulate an array with 33% probability
            if (random.randBool(.33) && topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.ARRAY[VarType.INT]))
                parent.add(genArrayManipulation(null, scope))
            //standard int manipulation
            else
                parent.add(Assignment(manipulateVar, genIntEvaluation(scope, manipulateVar)))
            count++
        }
    }

    //TODO consolidate array manipulations
    private fun genArrayManipulation(i: Evaluation<IntVar>?, scope: GenScope): ArrayAssign<*> {
        val arr = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
        val index = i ?: IntVar[random.randEasyInt(0, scope.getArrLength(arr) - 1)].asEval()

        return when {
            random.randBool() && scope.hasVarType(VarType.INT) -> {
                ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arr),
                        index,
                        Variable(VarType.INT, scope.getRandVar(VarType.INT)!!)
                )
            }
            else -> {
                val srcArr = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
                val srcIndex = random.randEasyInt(0, scope.getArrLength(srcArr) - 1)
                ArrayAssign(
                        Variable(VarType.ARRAY[VarType.INT], arr),
                        index,
                        ArrayAccess(VarType.INT,
                                Variable(VarType.ARRAY[VarType.INT], srcArr),
                                IntVar[srcIndex].asEval()
                        )
                )
            }
        }
    }

    private fun forLoopManip(context: GenContext, scope: GenScope): Assignment {
        var opType = RandomTypes.operationType(random.difficulty, random)
        //TODO find a better fix for divide by 0 and huge multiplication
        if (opType == MathOperation.Type.DIVIDE || opType == MathOperation.Type.MODULO || opType == MathOperation.Type.MULTIPLY)
            opType = MathOperation.Type.ADD
        val op = MathOperation(
                opType,
                Variable(VarType.INT, context.mainIntVar!!),
                Variable(VarType.INT, scope.getRandUnmanipVar(VarType.INT)!!)
        )
        return Assignment(context.mainIntVar!!, op)
    }

}