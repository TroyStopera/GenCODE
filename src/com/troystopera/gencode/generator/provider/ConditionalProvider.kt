package com.troystopera.gencode.generator.provider

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.components.Conditional
import com.troystopera.gencode.code.statements.evaluations.Comparison
import com.troystopera.gencode.code.statements.evaluations.Variable
import com.troystopera.gencode.exec.Executable
import com.troystopera.gencode.generator.*
import java.util.*

internal class ConditionalProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.CONDITIONAL, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ConditionalProvider = ConditionalProvider(difficulty, random.nextLong(), topics)

    override fun generate(parentType:Component.Type, varProvider: VariableProvider, record: GenRecord): ProviderResult {
        val comparisons = mutableListOf<Comparison<IntVar>>()
        val blocks = mutableListOf<CodeBlock>()
        val conditional = Conditional()

        //TODO() Add conditional ints (such as matching ints)

        //used to prevent conditionals comparing two int literals
        val defaultIntEval = {
            if (record.hasVarType(VarType.INT_PRIMITIVE))
                Variable.of<IntVar>(record.getRandVar(VarType.INT_PRIMITIVE))
            else easyIntEval.invoke()
        }

        while (comparisons.size < MIN_BRANCHES || (comparisons.size < MAX_BRANCHES && randHardBool()))
            comparisons.add(Comparison(
                    RandomTypes.comparisonType(random),
                    genIntEvaluation(record, defaultIntEval),
                    genIntEvaluation(record)
            ))

        //determine whether to add an else branch
        if (randBool()) {
            //if true removes a random branch to keep the total branch number the same
            if (comparisons.size >= MAX_BRANCHES || randEasyBool())
                comparisons.removeAt(randInt(0, comparisons.size - 1))

            val block = CodeBlock()
            conditional.setElse(block)
            blocks.add(block)
        }

        //add all branches
        for (comparison in comparisons) {
            val block = CodeBlock()
            conditional.addBranch(comparison, block)
            blocks.add(block)
        }

        return ProviderResult(conditional, blocks.toTypedArray(), record)
    }

}