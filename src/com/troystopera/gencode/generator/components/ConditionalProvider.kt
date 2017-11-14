package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.components.Conditional
import com.troystopera.gencode.code.statements.Evaluation
import com.troystopera.gencode.code.statements.evaluations.Comparison
import com.troystopera.gencode.code.statements.evaluations.ComparisonType
import com.troystopera.gencode.code.statements.evaluations.Variable
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import java.util.*

internal class ConditionalProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.CONDITIONAL, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ConditionalProvider = ConditionalProvider(difficulty, random.nextLong(), topics)

    override fun generate(parentType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val comparisons = mutableListOf<Comparison<IntVar>>()
        val blocks = mutableListOf<CodeBlock>()
        val conditional = Conditional()

        //TODO() Add conditional ints (such as matching ints)

        while (comparisons.size < MIN_BRANCHES || (comparisons.size < MAX_BRANCHES && randHardBool())) {
            val compType = RandomTypes.comparisonType(random)
            val eval = genEval(compType, scope)
            comparisons.add(Comparison(compType, eval.first, eval.second))
        }

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

        return Result(conditional, blocks.toTypedArray(), scope)
    }

    private fun genEval(compType: ComparisonType, scope: GenScope): Pair<Evaluation<IntVar>, Evaluation<IntVar>> {
        //the name of the int variable to compare
        val name = scope.getRandVar(VarType.INT_PRIMITIVE)!!

        //for '>' and '<' allow for a chance to compare the variable to itself
        if ((compType == ComparisonType.GREATER_THEN || compType == ComparisonType.LESS_THAN) &&
                //only likely <50% of the time and if the difficulty is low
                (randEasyBool() && randBool())) {
            return Pair(Variable.of(name), Variable.of(name))
        }

        //used to prevent conditionals comparing two int literals
        val defaultIntEval = {
            if (scope.hasVarType(VarType.INT_PRIMITIVE, ignore = name))
                Variable.of<IntVar>(scope.getRandVar(VarType.INT_PRIMITIVE, ignore = name))
            else easyIntEval.invoke()
        }

        val eval = genIntEvaluation(scope, defaultIntEval, ignore = name)

        return if (randBool()) Pair(Variable.of<IntVar>(name), eval)
        else Pair(eval, Variable.of(name))
    }

}