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

internal object DeclarationProvider : StatementProvider(ProviderType.DECLARATION) {

    override fun populate(parent: CodeBlock, scope: GenScope, context: GenContext) {
        var count = 0
        val arrays = context.topics.contains(ProblemTopic.ARRAY)

        //declare an array if needed
        if (arrays) {
            parent.add(declareArray(scope, context))
            parent.add(declareInt(scope, context))
            count += 2
        }

        //continue declaring until random end
        while (count < MIN_DECLARATIONS || (count < MAX_DECLARATIONS && context.random.randHardBool())) {
            if (arrays && context.random.randHardBool())
                parent.add(declareArray(scope, context))
            else
                parent.add(declareInt(scope, context))
            count++
        }
    }

    private fun declareInt(scope: GenScope, context: GenContext): Declaration<*> {
        val name = context.variableProvider.nextVar()
        scope.addVar(name, VarType.INT)
        return Declaration(
                VarType.INT,
                name,
                //TODO allow for declarations to be something other than an int literal
                IntVar[context.random.simpleInt()].asEval()
        )
    }

    private fun declareArray(scope: GenScope, context: GenContext): Declaration<*> {
        val name = context.variableProvider.nextVar()
        val length = context.random.randInt(MIN_ARRAY_LENGTH, MAX_ARRAY_LENGTH)
        scope.addArrVar(name, VarType.ARRAY[VarType.INT], length)
        return Declaration(
                VarType.ARRAY[VarType.INT],
                name,
                ArrayVar<IntVar>(
                        VarType.INT,
                        Array(length, { IntVar[context.random.simpleInt()] })).asEval()
        )
    }

}