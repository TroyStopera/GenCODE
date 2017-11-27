package com.troystopera.gencode.generator

import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.ProblemType
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.BlankLine
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.Function
import com.troystopera.gencode.code.statements.Return
import com.troystopera.gencode.generator.components.ComponentProvider
import com.troystopera.gencode.generator.statements.DeclarationProvider
import com.troystopera.gencode.generator.statements.ManipulationProvider
import com.troystopera.gencode.generator.statements.ReturnIntProvider
import java.util.*

class CodeGenerator private constructor(
        private val random: WeightedRandom,
        vararg private val topics: ProblemTopic
) {

    private val providers: List<ComponentProvider>
    private val declarationProvider = DeclarationProvider(random, topics)
    private val returnIntProvider = ReturnIntProvider(random, topics)
    private val manipulationProvider = ManipulationProvider(random, topics)

    init {
        val p = topics.distinct()
                .filter { ComponentProvider.hasProvider(it) }
                .map { ComponentProvider.fromTopic(it, topics, random) }.toMutableList()
        //TODO create array provider so that there is a guarantee of having providers
        if (p.isEmpty())
            p.add(ComponentProvider.fromTopic(ProblemTopic.FOR_LOOP, topics, random))
        providers = p
    }

    constructor(seed: Long, vararg topics: ProblemTopic) : this(WeightedRandom(seed), *topics)

    constructor(vararg topics: ProblemTopic) : this(Random().nextLong(), *topics)

    fun generate(minDif: Double, maxDif: Double, count: Int): SortedSet<Problem> {
        val set = sortedSetOf<Problem>()
        val step: Double = (maxDif - minDif) / count
        var difficulty = minDif

        for (i in 1..count) {
            set.add(generate(difficulty))
            difficulty += step
        }

        return set
    }

    fun generate(difficulty: Double, count: Int): List<Problem> {
        val list = mutableListOf<Problem>()
        for (i in 1..count) list.add(generate(difficulty))
        return list
    }

    fun generate(difficulty: Double): Problem {
        random.difficulty = difficulty
        val context = GenContext()
        val provider = VarNameProvider()
        val rootRecord = GenScope(random)
        val builder = Problem.Builder()
        builder.setType(ProblemType.RETURN_VALUE)
        builder.setTopics(*topics)
        builder.setDifficulty(difficulty)

        val main = Function("main", VarType.INT_PRIMITIVE)
        declarationProvider.populate(main, Component.Type.GENERIC, provider, rootRecord, context)
        context.mainIntVar = rootRecord.getRandVar(VarType.INT_PRIMITIVE)
        main.addExecutable(BlankLine.get())
        main.addExecutable(gen(provider, rootRecord, 1, context))
        //add a default return to ensure a complete program
        main.addExecutable(Return.returnStmt(context.mainIntVar ?: rootRecord.getRandVar(VarType.INT_PRIMITIVE)))

        builder.setMainFunction(main)
        return builder.build()
    }

    private fun gen(variableProvider: VarNameProvider, scope: GenScope, nestDepth: Int, context: GenContext): Component {
        val baseComponentResult =
                providers[random.nextInt(providers.size)].generate(Component.Type.GENERIC, variableProvider, scope, context)

        for (block in baseComponentResult.newBlocks) {
            if (nestDepth < MAX_NESTING_DEPTH && random.randHardBool()) {
                block.addExecutable(gen(
                        variableProvider,
                        baseComponentResult.scope.createChildRecord(baseComponentResult.component::class),
                        nestDepth + 1,
                        context)
                )
            } else //if (baseComponentResult.component is ForLoop || random.randEasyBool())
                manipulationProvider.populate(block, baseComponentResult.component.type, variableProvider, baseComponentResult.scope, context)
           // else
               // returnIntProvider.populate(block, baseComponentResult.component.type, variableProvider, baseComponentResult.scope, context)
        }

        return baseComponentResult.component
    }

}