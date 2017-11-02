package com.troystopera.gencode.generator.provider

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Assignment
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
import com.troystopera.gencode.generator.*
import java.util.*

internal class ManipulationProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : CodeProvider(ProviderType.MANIPULATION, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ManipulationProvider = ManipulationProvider(difficulty, random.nextLong(), topics)

    override fun generate(varProvider: VariableProvider, record: GenRecord): ProviderResult {
        if (!record.hasVarType(VarType.INT_PRIMITIVE))
            throw GenerationException(IllegalStateException("No ints in record passed to ManipulationProvider"))
        val block = CodeBlock()

        var count = 0
        while (count < MIN_OPERATIONS || (count < MAX_OPERATIONS && randHardBool())) {
            val manipulateVar = record.getRandVar(VarType.INT_PRIMITIVE)
            //potentially manipulate an array with ~33% probability
            if (randHardBool(.33) && topics.contains(ProblemTopic.ARRAY) && record.hasVarType(VarType.INT_ARRAY))
                block.addExecutable(genArrayManipulation(record))
            //standard int manipulation
            else
                block.addExecutable(Assignment.assign(manipulateVar, genIntEvaluation(record)))
            count++
        }

        return ProviderResult(block, emptyArray(), record)
    }

    private fun genArrayManipulation(record: GenRecord): Assignment {
        val arr = record.getRandVar(VarType.INT_ARRAY)!!
        val index = randEasyInt(0, record.getArrLength(arr) - 1)
        val rand = randInt(1, 3)

        return when {
            rand == 1 && record.hasVarType(VarType.INT_PRIMITIVE) -> {
                Assignment.assignArray(arr, index, record.getRandVar(VarType.INT_PRIMITIVE))
            }
            rand == 2 -> {
                Assignment.assignArray(arr, index, IntVar.of(randInt(0, 50)))
            }
            else -> {
                val srcArr = record.getRandVar(VarType.INT_ARRAY)!!
                val srcIndex = randEasyInt(0, record.getArrLength(srcArr) - 1)
                Assignment.assignArray(arr, index, ArrayAccess.access(srcArr, srcIndex))
            }
        }
    }

}