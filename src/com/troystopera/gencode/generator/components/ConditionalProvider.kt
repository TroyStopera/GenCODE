package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.gencode.generator.constraints.ConditionalConstraints
import com.troystopera.jkode.Component
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.Conditional
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal class ConditionalProvider(
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.CONDITIONAL, random, topics) {

    override fun generate(varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val comparisons = mutableListOf<Comparison<IntVar>>()
        val blocks = mutableListOf<CodeBlock>()
        val conditional = Conditional.Builder()

        for (i in 1..ConditionalConstraints.getBranchCount(random)) {
            val compType = ConditionalConstraints.getCompType(random)
            val eval = genEval(compType, scope)
            comparisons.add(Comparison(compType, eval.first, eval.second))
        }

        //determine whether to add an else branch
        if (ConditionalConstraints.useElse(random)) {
            //if true removes a random branch to keep the total branch number the same
            if (comparisons.size >= MAX_BRANCHES || random.randEasyBool())
                comparisons.removeAt(random.randInt(0, comparisons.size - 1))

            val block = CodeBlock()
            conditional.setElseBlock(block)
            blocks.add(block)
        }

        //add all branches
        for (comparison in comparisons) {
            val block = CodeBlock()
            conditional.addBranch(comparison, block)
            blocks.add(block)
        }

        return Result(conditional.build(), blocks.toTypedArray(), scope)
    }

    private fun genEval(compType: Comparison.Type, scope: GenScope): Pair<Evaluation<IntVar>, Evaluation<IntVar>> {
        //the name of the int variable to compare
        val name = scope.getRandVar(VarType.INT)!!

        //certain conditions will lead to comparing a variable to itself
        if (ConditionalConstraints.useSelfCompare(random, compType))
            return Pair(Variable(VarType.INT, name), Variable(VarType.INT, name))

        val eval = if (ConditionalConstraints.useExpression(random)) {
            //get a variable separate from the comparison variable if possible
            val mathVar = scope.getRandVar(VarType.INT, name) ?: scope.getRandVar(VarType.INT)!!
            genMathOperation(scope, mathVar, RandomTypes.operationType(random.difficulty, random))
        } else {
            if (!ConditionalConstraints.useIntLiteral(random)) {
                val compVar = scope.getRandVar(VarType.INT, name)
                if (compVar != null) Variable(VarType.INT, compVar)
                else IntVar[random.randInt(0, 25)].asEval()
            } else IntVar[random.randInt(0, 25)].asEval()
        }

        return if (random.randBool()) Pair(Variable(VarType.INT, name), eval)
        else Pair(eval, Variable(VarType.INT, name))
    }

}