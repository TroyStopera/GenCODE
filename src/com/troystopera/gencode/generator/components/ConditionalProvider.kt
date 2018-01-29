package com.troystopera.gencode.generator.components

import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.constraints.ConditionalConstraints
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.Conditional
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object ConditionalProvider : ComponentProvider(ProviderType.CONDITIONAL) {

    override fun generate(scope: GenScope, context: GenContext): Result {
        val comparisons = mutableListOf<Comparison<IntVar>>()
        val blocks = mutableListOf<CodeBlock>()
        val conditional = Conditional.Builder()

        for (i in 1..ConditionalConstraints.getBranchCount(context.random)) {
            val compType = ConditionalConstraints.getCompType(context.random)
            val eval = genEval(compType, context, scope)
            comparisons.add(Comparison(compType, eval.first, eval.second))
        }

        //determine whether to add an else branch
        if (ConditionalConstraints.useElse(context.random)) {
            //if true removes a random branch to keep the total branch number the same
            if (comparisons.size >= MAX_BRANCHES || context.random.randEasyBool())
                comparisons.removeAt(context.random.randInt(0, comparisons.size - 1))

            val block = CodeBlock()
            conditional.setElseBlock(block)
            blocks.add(block)
        }

        //add all branches
        for (comparison in comparisons) {
            val branch = Conditional.Branch(comparison)
            conditional.addBranch(branch)
            blocks.add(branch)
        }

        return Result(conditional.build(), blocks.toTypedArray(), scope)
    }

    private fun genEval(compType: Comparison.Type, context: GenContext, scope: GenScope): Pair<Evaluation<IntVar>, Evaluation<IntVar>> {
        //the name of the int variable to compare
        val name = scope.getRandVar(VarType.INT)!!

        //certain conditions will lead to comparing a variable to itself
        if (ConditionalConstraints.useSelfCompare(context.random, compType))
            return Pair(Variable(VarType.INT, name), Variable(VarType.INT, name))

        val eval = if (ConditionalConstraints.useExpression(context.random)) {
            //get a variable separate from the comparison variable if possible
            val mathVar = scope.getRandVar(VarType.INT, name) ?: scope.getRandVar(VarType.INT)!!
            genMathOperation(context, scope, mathVar, RandomTypes.operationType(context.random.difficulty, context.random))
        } else {
            if (!ConditionalConstraints.useIntLiteral(context.random)) {
                val compVar = scope.getRandVar(VarType.INT, name)
                if (compVar != null) Variable(VarType.INT, compVar)
                else IntVar[context.random.randInt(0, 25)].asEval()
            } else IntVar[context.random.randInt(0, 25)].asEval()
        }

        return if (context.random.randBool()) Pair(Variable(VarType.INT, name), eval)
        else Pair(eval, Variable(VarType.INT, name))
    }

}