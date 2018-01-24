package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.jkode.Component
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.statements.Declaration
import com.troystopera.jkode.vars.ArrayVar
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal class DeclarationProvider(
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.DECLARATION, random, topics) {

    override fun populate(parent: CodeBlock, varProvider: VarNameProvider, scope: GenScope, context: GenContext) {
        var count = 0
        val arrays = topics.contains(ProblemTopic.ARRAY)

        //declare an array if needed
        if (arrays) {
            parent.add(declareArray(scope, varProvider))
            parent.add(declareInt(scope, varProvider))
            count += 2
        }

        //continue declaring until random end
        while (count < MIN_DECLARATIONS || (count < MAX_DECLARATIONS && random.randHardBool())) {
            if (arrays && random.randHardBool())
                parent.add(declareArray(scope, varProvider))
            else
                parent.add(declareInt(scope, varProvider))
            count++
        }
    }

    private fun declareInt(scope: GenScope, varProvider: VarNameProvider): Declaration<*> {
        val name = varProvider.nextVar()
        scope.addVar(name, VarType.INT)
        return Declaration(
                VarType.INT,
                name,
                //TODO allow for declarations to be something other than an int literal
                IntVar[random.simpleInt()].asEval()
        )
    }

    private fun declareArray(scope: GenScope, varProvider: VarNameProvider): Declaration<*> {
        val name = varProvider.nextVar()
        val length = random.randInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH)
        scope.addArrVar(name, VarType.ARRAY[VarType.INT], length)
        return Declaration(
                VarType.ARRAY[VarType.INT],
                name,
                ArrayVar<IntVar>(
                        VarType.INT,
                        Array(length, { IntVar[random.simpleInt()] })).asEval()
        )
    }

}