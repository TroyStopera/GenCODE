package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.Var
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Return
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import java.util.*

internal class ReturnIntProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.RETURN, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ReturnIntProvider = ReturnIntProvider(difficulty, random.nextLong(), topics)

    override fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext) {
        //TODO add history for returned array variables
        //50% chance of array access if possible
        if (randBool() && topics.contains(ProblemTopic.ARRAY) && scope.hasVarType(VarType.INT_ARRAY)) {
            val arrName = scope.getRandVar(VarType.INT_ARRAY)
            val length = scope.getArrLength(arrName!!)
            parent.addExecutable(Return.returnStmt(ArrayAccess.access(arrName, randInt(0, length - 1))))
        }
        //otherwise prefer to return a variable, but default to an easy int
        else {
            when {
            //always prefer to return a variable that hasn't been returned yet
                scope.hasVarType(VarType.INT_PRIMITIVE, *scope.history.getReturnedVars()) -> {
                    val varName = scope.getRandVar(VarType.INT_PRIMITIVE, *scope.history.getReturnedVars())!!
                    parent.addExecutable(Return.returnStmt(varName))
                    scope.history.addReturnedVar(varName)
                }
            //otherwise try for a variable that has been returned
                scope.hasVarType(VarType.INT_PRIMITIVE) ->
                    parent.addExecutable(Return.returnStmt(scope.getRandVar(VarType.INT_PRIMITIVE)))
            //default to int literal
                else ->
                    parent.addExecutable(Return.returnStmt(Var.random(VarType.INT_PRIMITIVE, random)))
            }
        }
    }

}