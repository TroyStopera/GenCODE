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
import com.troystopera.gencode.code.statements.evaluations.Value
import com.troystopera.gencode.code.statements.evaluations.Variable
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.gencode.generator.constraints.ConditionalConstraints

internal class ConditionalProvider(
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.CONDITIONAL, random, topics) {

    override fun generate(parentType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val comparisons = mutableListOf<Comparison<IntVar>>()
        val blocks = mutableListOf<CodeBlock>()
        val conditional = Conditional()

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

        //certain conditions will lead to comparing a variable to itself
        if (ConditionalConstraints.useSelfCompare(random, compType))
            return Pair(Variable.of(name), Variable.of(name))

        val eval = if (ConditionalConstraints.useExpression(random)) {
            //get a variable separate from the comparison variable if possible
            val mathVar = scope.getRandVar(VarType.INT_PRIMITIVE, name) ?: scope.getRandVar(VarType.INT_PRIMITIVE)!!
            genMathOperation(scope, mathVar, RandomTypes.operationType(random.difficulty, random))
        } else {
            if (!ConditionalConstraints.useIntLiteral(random)) {
                val compVar = scope.getRandVar(VarType.INT_PRIMITIVE, name)
                if (compVar != null) Variable.of<IntVar>(compVar)
                else Value.of(IntVar.of(random.randInt(0, 25)))
            } else Value.of(IntVar.of(random.randInt(0, 25)))
        }

        return if (random.randBool()) Pair(Variable.of<IntVar>(name), eval)
        else Pair(eval, Variable.of(name))
    }

}