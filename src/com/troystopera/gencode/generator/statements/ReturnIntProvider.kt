package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.control.Return
import com.troystopera.jkode.evaluations.ArrayAccess
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object ReturnIntProvider : StatementProvider(ProviderType.RETURN) {

    override fun populate(parent: CodeBlock, scope: GenScope, context: GenContext) {
        //TODO add history for returned array variables
        //50% chance of array access if possible
        if (context.random.randBool() && context.topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.ARRAY[VarType.INT])) {
            val arrName = scope.getRandVar(VarType.INT)
            val length = scope.getArrLength(arrName!!)
            parent.add(
                    Return(ArrayAccess(
                            VarType.INT,
                            Variable(VarType.ARRAY[VarType.INT], arrName),
                            IntVar[context.random.randInt(0, length - 1)].asEval())
                    )
            )
        }
        //otherwise prefer to return a variable, but default to an easy int
        else {
            when {
            //always prefer to return a variable that hasn't been returned yet
                scope.hasVarType(VarType.INT, *scope.history.getReturnedVars()) -> {
                    val varName = scope.getRandVar(VarType.INT, *scope.history.getReturnedVars())!!
                    parent.add(Return(Variable(VarType.INT, varName)))
                    scope.history.addReturnedVar(varName)
                }
            //otherwise try for a variable that has been returned
                scope.hasVarType(VarType.INT) ->
                    parent.add(Return(Variable(VarType.INT, scope.getRandVar(VarType.INT)!!)))
            //default to int literal
                else -> parent.add(Return(IntVar[context.random.randInt()].asEval()))
            }
        }
    }

}