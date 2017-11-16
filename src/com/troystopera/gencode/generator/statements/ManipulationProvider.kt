package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Assignment
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
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
        while (count < MIN_OPERATIONS || (count < MAX_OPERATIONS && random.randHardBool())) {
            val manipulateVar = scope.getRandVar(VarType.INT_PRIMITIVE)!!
            //potentially manipulate an array with 33% probability
            if (random.randBool(.33) && topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.INT_ARRAY))
                parent.addExecutable(genArrayManipulation(scope))
            //standard int manipulation
            else
                parent.addExecutable(Assignment.assign(manipulateVar, genIntEvaluation(scope, manipulateVar)))
            count++
        }
    }

    private fun genArrayManipulation(scope: GenScope): Assignment {
        val arr = scope.getRandVar(VarType.INT_ARRAY)!!
        val index = random.randEasyInt(0, scope.getArrLength(arr) - 1)
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

}