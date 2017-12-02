package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.ArrayVar
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Declaration
import com.troystopera.gencode.code.statements.evaluations.Value
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import java.util.*

internal class DeclarationProvider(
        random: WeightedRandom,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.DECLARATION, random, topics) {

    override fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext) {
        var count = 0
        val arrays = topics.contains(ProblemTopic.ARRAY)

        //declare an array if needed
        if (arrays) {
            parent.addExecutable(declareArray(scope, varProvider))
            parent.addExecutable(declareInt(scope, varProvider))
            count += 2
        }

        //continue declaring until random end
        while (count < MIN_DECLARATIONS || (count < MAX_DECLARATIONS && random.randHardBool())) {
            if (arrays && random.randHardBool())
                parent.addExecutable(declareArray(scope, varProvider))
            else
                parent.addExecutable(declareInt(scope, varProvider))
            count++
        }
    }

    private fun declareInt(scope: GenScope, varProvider: VarNameProvider): Declaration {
        val name = varProvider.nextVar()
        scope.addVar(name, VarType.INT_PRIMITIVE)
        return Declaration.declareWithAssign(
                name,
                VarType.INT_PRIMITIVE,
                //TODO allow for declarations to be something other than an int literal
                Value.of(IntVar.of(random.simpleInt()))
        )
    }

    private fun declareArray(scope: GenScope, varProvider: VarNameProvider): Declaration {
        val name = varProvider.nextVar()
        val length = random.randInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH)
        scope.addArrVar(name, VarType.INT_ARRAY, length)
        return Declaration.declareWithAssign(
                name,
                VarType.INT_ARRAY,
                Value.of(ArrayVar.of(*Array<IntVar>(length, { IntVar.of(random.simpleInt()) })))
        )
    }

}