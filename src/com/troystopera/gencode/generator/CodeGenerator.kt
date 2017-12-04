package com.troystopera.gencode.generator

import com.sun.org.apache.bcel.internal.classfile.Code
import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.ProblemType
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.BlankLine
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.components.ForLoop
import com.troystopera.gencode.code.components.Function
import com.troystopera.gencode.code.statements.Return
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
import com.troystopera.gencode.generator.components.ComponentProvider
import com.troystopera.gencode.generator.components.ConditionalProvider
import com.troystopera.gencode.generator.components.ForLoopProvider
import com.troystopera.gencode.generator.statements.DeclarationProvider
import com.troystopera.gencode.generator.statements.ManipulationProvider
import com.troystopera.gencode.generator.statements.ReturnIntProvider
import java.util.*

class CodeGenerator private constructor(
        private val random: DifficultyRandom,
        vararg private val topics: ProblemTopic
) {

    private val declarationProvider = DeclarationProvider(random, topics)
    private val manipulationProvider = ManipulationProvider(random, topics)

    constructor(seed: Long, vararg topics: ProblemTopic) : this(DifficultyRandom(seed), *topics)

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
        val nestPattern = NestStructure.get(hashSetOf(*topics), difficulty, random)
        val context = GenContext()
        val provider = VarNameProvider()
        val rootRecord = GenScope(random)
        val builder = Problem.Builder()
        builder.setType(ProblemType.RETURN_VALUE)
        builder.setTopics(*topics)
        builder.setDifficulty(difficulty)
        rootRecord.addPattern(nestPattern)

        val main = Function("example", VarType.INT_PRIMITIVE)
        declarationProvider.populate(main, Component.Type.GENERIC, provider, rootRecord, context)
        context.mainIntVar = rootRecord.getRandVar(VarType.INT_PRIMITIVE)
        main.addExecutable(BlankLine.get())
        main.addExecutable(gen(provider, rootRecord, context, nestPattern))
        //add a default return to ensure a complete program
        if (context.mainArray != null) {
            val array = context.mainArray!!
            val length = rootRecord.getArrLength(array)
            main.addExecutable(Return.returnStmt(ArrayAccess.access(array, random.nextInt(length))))
        } else
            main.addExecutable(Return.returnStmt(context.mainIntVar ?: rootRecord.getRandVar(VarType.INT_PRIMITIVE)))

        builder.setMainFunction(main)
        return builder.build()
    }

    private fun gen(variableProvider: VarNameProvider, scope: GenScope, context: GenContext, nestStructure: NestStructure): Component {
        when (nestStructure) {
            is NestStructure.NestedLoop -> {
                val provider = ForLoopProvider(random, topics)
                val block = CodeBlock()
                var temp = block
                var genScope = scope
                for (i in 1..nestStructure.depth) {
                    val result = provider.generate(temp.type, variableProvider, genScope, context)
                    temp.addExecutable(result.component)
                    temp = result.component as ForLoop
                    genScope = result.scope
                }
                manipulationProvider.populate(temp, temp.type, variableProvider, genScope, context)
                return block
            }

            is NestStructure.NestedConditional -> {
                val provider = ConditionalProvider(random, topics)
                //outer conditional
                val conditional = provider.generate(Component.Type.GENERIC, variableProvider, scope, context)

                //1st nest
                for (b1 in conditional.newBlocks) {
                    //possibly don't nest
                    if (random.randEasyBool()) {
                        manipulationProvider.populate(b1, conditional.component.type, variableProvider, conditional.scope, context)
                    } else {
                        val c1 = provider.generate(Component.Type.CONDITIONAL, variableProvider, conditional.scope, context)
                        b1.addExecutable(c1.component)

                        //add 2nd nest if needed
                        for (b2 in c1.newBlocks) {
                            if (nestStructure.depth > 2 && random.nextBoolean()) {
                                val c2 = provider.generate(Component.Type.CONDITIONAL, variableProvider, c1.scope, context)
                                b2.addExecutable(c2.component)

                                for (b3 in c2.newBlocks)
                                    manipulationProvider.populate(b3, c2.component.type, variableProvider, c2.scope, context)
                            } else manipulationProvider.populate(b2, c1.component.type, variableProvider, c1.scope, context)
                        }
                    }
                }
                return conditional.component
            }

            is NestStructure.NestedLoopConditional -> {
                val loopProvider = ForLoopProvider(random, topics)
                val condProvider = ConditionalProvider(random, topics)

                val block = CodeBlock()
                var loopResult = loopProvider.generate(Component.Type.GENERIC, variableProvider, scope, context)
                var loop = loopResult.component
                var s = loopResult.scope
                block.addExecutable(loop)

                //add second for loop if needed
                if (nestStructure.depth > 2) {
                    loopResult = loopProvider.generate(loop.type, variableProvider, s, context)
                    (loop as CodeBlock).addExecutable(loopResult.component)
                    loop = loopResult.component
                    s = loopResult.scope
                }

                //add conditional
                val condResult = condProvider.generate(loop.type, variableProvider, s, context)
                (loop as CodeBlock).addExecutable(condResult.component)
                for (b in condResult.newBlocks)
                    manipulationProvider.populate(b, condResult.component.type, variableProvider, condResult.scope, context)
                return block
            }

            NestStructure.Companion.SingleLoop -> {
                val result = ForLoopProvider(random, topics).generate(Component.Type.GENERIC, variableProvider, scope, context)
                val loop = result.component as CodeBlock
                val s = result.scope
                manipulationProvider.populate(loop, loop.type, variableProvider, s, context)
                return loop
            }

            NestStructure.Companion.SingleConditional -> {
                val result = ConditionalProvider(random, topics).generate(Component.Type.GENERIC, variableProvider, scope, context)
                for (block in result.newBlocks)
                    manipulationProvider.populate(block, result.component.type, variableProvider, result.scope, context)
                return result.component
            }

            NestStructure.Companion.ComboLoopConditional -> {
                val block = CodeBlock()
                val loop = ForLoopProvider(random, topics).generate(Component.Type.GENERIC, variableProvider, scope, context)
                manipulationProvider.populate(loop.component as CodeBlock, loop.component.type, variableProvider, loop.scope, context)
                val conditional = ConditionalProvider(random, topics).generate(Component.Type.GENERIC, variableProvider, scope, context)
                for (b in conditional.newBlocks)
                    manipulationProvider.populate(b, conditional.component.type, variableProvider, conditional.scope, context)
                block.addExecutable(loop.component)
                block.addExecutable(BlankLine.get())
                block.addExecutable(conditional.component)
                return block
            }
        }
    }

}