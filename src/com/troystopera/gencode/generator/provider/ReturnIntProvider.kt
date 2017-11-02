package com.troystopera.gencode.generator.provider

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.Var
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Return
import com.troystopera.gencode.code.statements.evaluations.ArrayAccess
import com.troystopera.gencode.generator.*
import java.util.*

internal class ReturnIntProvider(
        difficulty: Double,
        seed: Long,
        topics: Array<out ProblemTopic>
) : CodeProvider(ProviderType.RETURN, difficulty, Random(seed), topics) {

    override fun withDifficulty(difficulty: Double): ReturnIntProvider = ReturnIntProvider(difficulty, random.nextLong(), topics)

    override fun generate(varProvider: VariableProvider, record: GenRecord): ProviderResult {
        val block = CodeBlock()

        //50% chance of array access if possible
        if (randBool() && topics.contains(ProblemTopic.ARRAY) && record.hasVarType(VarType.INT_ARRAY)) {
            val arrName = record.getRandVar(VarType.INT_ARRAY)
            val length = record.getArrLength(arrName!!)
            block.addExecutable(Return.returnStmt(ArrayAccess.access(arrName, randInt(0, length - 1))))
        }
        //on easier difficulty return a literal value
        else if (randEasyBool() || !record.hasVarType(VarType.INT_PRIMITIVE)) {
            block.addExecutable(Return.returnStmt(Var.random(VarType.INT_PRIMITIVE, random)))
        }
        //otherwise return a variable
        else {
            block.addExecutable(Return.returnStmt(record.getRandVar(VarType.INT_PRIMITIVE)))
        }

        return ProviderResult(block, emptyArray(), record)
    }

}