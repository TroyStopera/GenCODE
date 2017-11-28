package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.components.ForLoop
import com.troystopera.gencode.code.statements.Assignment
import com.troystopera.gencode.code.statements.Evaluation
import com.troystopera.gencode.code.statements.evaluations.*
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import java.util.*

internal class ManipulationProvider(
        random: WeightedRandom,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.MANIPULATION, random, topics) {

    override fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext) {
        if (!scope.hasVarType(VarType.INT_PRIMITIVE))
            throw GenerationException(IllegalStateException("No ints in scope passed to ManipulationProvider"))

        var count = 0

        //start by checking for an array generation pattern
        if (scope.hasPattern(Pattern.Type.ARRAY_WALK)) {
            val arrayWalk = scope.getPattern(Pattern.Type.ARRAY_WALK)!! as Pattern.ArrayWalk
            parent.addExecutable(
                    Assignment.assignArray(
                            arrayWalk.arrayName,
                            Variable.of<IntVar>(arrayWalk.index),
                            Variable.of<IntVar>(scope.getRandUnmanipVar(VarType.INT_PRIMITIVE)!!)
                    )
            )
            count++
        }

        //manipulate the return var if present
        if (context.mainIntVar != null) {
            if (scope.isIn(ForLoop::class))
                parent.addExecutable(forLoopManip(context, scope))
            else
                parent.addExecutable(Assignment.assign(context.mainIntVar, genIntEvaluation(scope, context.mainIntVar!!)))
            count++
        }

        while (count < MIN_OPERATIONS || (count < MAX_OPERATIONS && random.randHardBool())) {
            val manipulateVar = scope.getRandVar(VarType.INT_PRIMITIVE)!!
            //potentially manipulate an array with 33% probability
            if (random.randBool(.33) && topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.INT_ARRAY))
                parent.addExecutable(genArrayManipulation(null, scope))
            //standard int manipulation
            else
                parent.addExecutable(Assignment.assign(manipulateVar, genIntEvaluation(scope, manipulateVar)))
            count++
        }
    }

    //TODO consolidate array manipulations
    private fun genArrayManipulation(i: Evaluation<IntVar>?, scope: GenScope): Assignment {
        val arr = scope.getRandVar(VarType.INT_ARRAY)!!
        val index = i ?: Value.of(IntVar.of(random.randEasyInt(0, scope.getArrLength(arr) - 1)))
        val rand = random.randInt(1, 3)

        return when {
            rand == 1 && scope.hasVarType(VarType.INT_PRIMITIVE) -> {
                Assignment.assignArray(arr, index, scope.getRandVar(VarType.INT_PRIMITIVE))
            }
            rand == 2 -> {
                Assignment.assignArray(arr, index, IntVar.of(random.randInt(0, 50)))
            }
            else -> {
                val srcArr = scope.getRandVar(VarType.INT_ARRAY)!!
                val srcIndex = random.randEasyInt(0, scope.getArrLength(srcArr) - 1)
                Assignment.assignArray(arr, index, ArrayAccess.access(srcArr, srcIndex))
            }
        }
    }

    private fun forLoopManip(context: GenContext, scope: GenScope): Assignment {
        var opType = RandomTypes.operationType(random.difficulty, random)
        //TODO find a better fix for divide by 0
        if (opType == OperationType.DIVISION || opType == OperationType.MODULUS)
            opType = OperationType.MULTIPLICATION
        val op = MathOperation(opType, context.mainIntVar, scope.getRandUnmanipVar(VarType.INT_PRIMITIVE))
        return Assignment.assign(context.mainIntVar, op)
    }

}