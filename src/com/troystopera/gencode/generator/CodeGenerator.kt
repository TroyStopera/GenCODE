package com.troystopera.gencode.generator

import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.ProblemType
import com.troystopera.gencode.generator.components.ConditionalProvider
import com.troystopera.gencode.generator.components.ForLoopProvider
import com.troystopera.gencode.generator.statements.DeclarationProvider
import com.troystopera.gencode.generator.statements.ManipulationProvider
import com.troystopera.jkode.BlankLine
import com.troystopera.jkode.Component
import com.troystopera.jkode.JFunction
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.control.Return
import com.troystopera.jkode.evaluations.ArrayAccess
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType
import java.util.*

class CodeGenerator private constructor(
        private val random: DifficultyRandom,
        vararg private val topics: ProblemTopic
) {

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
        val context = GenContext(random, topics.asList(), VarNameProvider())
        val rootRecord = GenScope(random)
        val builder = Problem.Builder()
        builder.setType(ProblemType.RETURN_VALUE)
        builder.setTopics(*topics)
        builder.setDifficulty(difficulty)
        rootRecord.addPattern(nestPattern)

        val main = JFunction(VarType.INT, "example")
        DeclarationProvider.populate(main.body, rootRecord, context)
        context.mainIntVar = rootRecord.getRandVar(VarType.INT)
        main.body.add(BlankLine)
        main.body.add(gen(context.variableProvider, rootRecord, context, nestPattern))
        //add a default return to ensure a complete program
        if (context.mainArray != null) {
            val array = context.mainArray!!
            val length = rootRecord.getArrLength(array)
            main.body.add(Return(ArrayAccess(VarType.INT, Variable(VarType.ARRAY, array), IntVar[random.nextInt(length)].asEval())))
        } else
            main.body.add(Return(Variable(VarType.INT, context.mainIntVar ?: rootRecord.getRandVar(VarType.INT)!!)))
        builder.setMainFunction(main)
        return builder.build()
    }

    private fun gen(variableProvider: VarNameProvider, scope: GenScope, context: GenContext, nestStructure: NestStructure): Component {
        when (nestStructure) {
            is NestStructure.NestedLoop -> {
                val block = CodeBlock()
                var temp = block
                var genScope = scope
                for (i in 1..nestStructure.depth) {
                    val result = ForLoopProvider.generate(genScope, context)
                    temp.add(result.component)
                    temp = result.component as ForLoop
                    genScope = result.scope
                }
                ManipulationProvider.populate(temp, genScope, context)
                return block
            }

            is NestStructure.NestedConditional -> {
                //outer conditional
                val conditional = ConditionalProvider.generate(scope, context)

                //1st nest
                for (b1 in conditional.newBlocks) {
                    //possibly don't nest
                    if (random.randEasyBool()) {
                        ManipulationProvider.populate(b1, conditional.scope, context)
                    } else {
                        val c1 = ConditionalProvider.generate(conditional.scope, context)
                        b1.add(c1.component)

                        //add 2nd nest if needed
                        for (b2 in c1.newBlocks) {
                            if (nestStructure.depth > 2 && random.nextBoolean()) {
                                val c2 = ConditionalProvider.generate(c1.scope, context)
                                b2.add(c2.component)

                                for (b3 in c2.newBlocks)
                                    ManipulationProvider.populate(b3, c2.scope, context)
                            } else ManipulationProvider.populate(b2, c1.scope, context)
                        }
                    }
                }
                return conditional.component
            }

            is NestStructure.NestedLoopConditional -> {
                val block = CodeBlock()
                var loopResult = ForLoopProvider.generate(scope, context)
                var loop = loopResult.component
                var s = loopResult.scope
                block.add(loop)

                //add second for loop if needed
                if (nestStructure.depth > 2) {
                    loopResult = ForLoopProvider.generate(s, context)
                    (loop as CodeBlock).add(loopResult.component)
                    loop = loopResult.component
                    s = loopResult.scope
                }

                //add conditional
                val condResult = ConditionalProvider.generate(s, context)
                (loop as CodeBlock).add(condResult.component)
                for (b in condResult.newBlocks)
                    ManipulationProvider.populate(b, condResult.scope, context)
                return block
            }

            NestStructure.Companion.SingleLoop -> {
                val result = ForLoopProvider.generate(scope, context)
                val loop = result.component as CodeBlock
                val s = result.scope
                ManipulationProvider.populate(loop, s, context)
                return loop
            }

            NestStructure.Companion.SingleConditional -> {
                val result = ConditionalProvider.generate(scope, context)
                for (block in result.newBlocks)
                    ManipulationProvider.populate(block, result.scope, context)
                return result.component
            }

            NestStructure.Companion.ComboLoopConditional -> {
                val block = CodeBlock()
                val loop = ForLoopProvider.generate(scope, context)
                ManipulationProvider.populate(loop.component as CodeBlock, loop.scope, context)
                val conditional = ConditionalProvider.generate(scope, context)
                for (b in conditional.newBlocks)
                    ManipulationProvider.populate(b, conditional.scope, context)
                block.add(loop.component)
                block.add(BlankLine)
                block.add(conditional.component)
                return block
            }
        }
    }

}