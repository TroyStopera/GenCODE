package com.troystopera.gencode.generator

import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.ProblemType
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.BlankLine
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.components.ForLoop
import com.troystopera.gencode.code.components.Function
import com.troystopera.gencode.generator.provider.DeclarationProvider
import com.troystopera.gencode.generator.provider.ManipulationProvider
import com.troystopera.gencode.generator.provider.ReturnIntProvider
import java.util.*

class CodeGenerator(
        difficulty: Double,
        seed: Long,
        vararg private val topics: ProblemTopic
) : WeightedRandom(difficulty, Random(seed)) {

    private val providers: List<ComponentProvider>
    private val declarationProvider = DeclarationProvider(difficulty, seed, topics)
    private val returnIntProvider = ReturnIntProvider(difficulty, random.nextLong(), topics)
    private val manipulationProvider = ManipulationProvider(difficulty, random.nextLong(), topics)

    init {
        val p = topics.distinct()
                .filter { ComponentProvider.hasProvider(it) }
                .map { ComponentProvider.fromTopic(it, difficulty, topics) }.toMutableList()
        //TODO create array provider so that there is a guarantee of having providers
        if (p.isEmpty())
            p.add(ComponentProvider.fromTopic(ProblemTopic.FOR_LOOP, difficulty, topics))
        providers = p
    }

    constructor(difficulty: Double, vararg topics: ProblemTopic) : this(difficulty, Random().nextLong(), *topics)

    fun generate(count: Int): List<Problem> {
        val list = mutableListOf<Problem>()
        for (i in 1..count)
            list.add(generate())
        return list
    }

    fun generate(): Problem {
        val provider = VariableProvider()
        val rootRecord = GenRecord()
        val builder = Problem.Builder()
        builder.setType(ProblemType.RETURN_VALUE)
        builder.setTopics(*topics)

        val main = Function("main", VarType.INT_PRIMITIVE)
        declarationProvider.populate(main, Component.Type.GENERIC, provider, rootRecord)
        main.addExecutable(gen(provider, rootRecord, 1))
        main.addExecutable(BlankLine.get())
        //add a default return to ensure a complete program
        returnIntProvider.populate(main, Component.Type.GENERIC, provider, rootRecord)

        builder.setMainFunction(main)
        return builder.build()
    }

    private fun gen(variableProvider: VariableProvider, record: GenRecord, nestDepth: Int): Component {
        val baseComponentResult =
                providers[randInt(0, providers.size - 1)].withDifficulty(difficulty / nestDepth)
                        .generate(Component.Type.GENERIC, variableProvider, record)

        for (block in baseComponentResult.newBlocks) {
            if (nestDepth < MAX_NESTING_DEPTH && randHardBool()) {
                block.addExecutable(gen(
                        variableProvider,
                        baseComponentResult.record.createChildRecord(),
                        nestDepth + 1)
                )
            } else if (baseComponentResult.component is ForLoop || randEasyBool())
                manipulationProvider.populate(block, baseComponentResult.component.type, variableProvider, baseComponentResult.record)
            else
                returnIntProvider.populate(block, baseComponentResult.component.type, variableProvider, baseComponentResult.record)
        }

        return baseComponentResult.component
    }

}