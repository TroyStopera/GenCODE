package com.troystopera.gencode.generator.provider

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.ArrayVar
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Declaration
import com.troystopera.gencode.code.statements.evaluations.Value
import com.troystopera.gencode.generator.*
import java.util.*

internal class DeclarationProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : CodeProvider(ProviderType.DECLARATION, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): DeclarationProvider = DeclarationProvider(difficulty, random.nextLong(), topics)

    override fun generate(varProvider: VariableProvider, record: GenRecord): ProviderResult {
        val block = CodeBlock()
        var count = 0

        //possibly declare an array
        if (topics.contains(ProblemTopic.ARRAY) && randBool()) {
            val name = varProvider.nextVar()
            val length = randInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH)
            block.addExecutable(Declaration.declareWithAssign(
                    name,
                    VarType.INT_ARRAY,
                    Value.of(ArrayVar.of(*Array<IntVar>(length, { IntVar.of(randInt()) })))
            ))
            record.addArrVar(name, VarType.INT_ARRAY, length)
            count++
        }
        //continue declaring until random end
        while (count < MIN_DECLARATIONS || (count < MAX_DECLARATIONS && randHardBool())) {
            val name = varProvider.nextVar()
            block.addExecutable(Declaration.declareWithAssign(
                    name,
                    VarType.INT_PRIMITIVE,
                    genIntEvaluation(record)
            ))
            record.addVar(name, VarType.INT_PRIMITIVE)
            count++
        }

        return ProviderResult(block, emptyArray(), record)
    }

}