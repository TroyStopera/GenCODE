package com.troystopera.gencode.generator.provider

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.Var
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Return
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
import com.troystopera.gencode.generator.*
import java.util.*

internal class ReturnIntProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : StatementProvider(ProviderType.RETURN, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ReturnIntProvider = ReturnIntProvider(difficulty, random.nextLong(), topics)

    override fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VariableProvider, record: GenRecord) {
        //TODO add history for returned array variables
        //50% chance of array access if possible
        if (randBool() && topics.contains(ProblemTopic.ARRAY) && record.hasVarType(VarType.INT_ARRAY)) {
            val arrName = record.getRandVar(VarType.INT_ARRAY)
            val length = record.getArrLength(arrName!!)
            parent.addExecutable(Return.returnStmt(ArrayAccess.access(arrName, randInt(0, length - 1))))
        }
        //otherwise prefer to return a variable, but default to an easy int
        else {
            when {
            //always prefer to return a variable that hasn't been returned yet
                record.hasVarType(VarType.INT_PRIMITIVE, *record.history.getReturnedVars()) -> {
                    val varName = record.getRandVar(VarType.INT_PRIMITIVE, *record.history.getReturnedVars())!!
                    parent.addExecutable(Return.returnStmt(varName))
                    record.history.addReturnedVar(varName)
                }
            //otherwise try for a variable that has been returned
                record.hasVarType(VarType.INT_PRIMITIVE) ->
                    parent.addExecutable(Return.returnStmt(record.getRandVar(VarType.INT_PRIMITIVE)))
            //default to int literal
                else ->
                    parent.addExecutable(Return.returnStmt(Var.random(VarType.INT_PRIMITIVE, random)))
            }
        }
    }

}