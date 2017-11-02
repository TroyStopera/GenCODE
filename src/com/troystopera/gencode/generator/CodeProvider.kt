package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.statements.Evaluation
import com.troystopera.gencode.code.statements.evaluations.MathOperation
import com.troystopera.gencode.code.statements.evaluations.OperationType
import com.troystopera.gencode.code.statements.evaluations.Value
import com.troystopera.gencode.code.statements.evaluations.Variable
import java.util.*

internal abstract class CodeProvider(
        val type: ProviderType,
        difficulty: Double,
        random: Random,
        protected val topics: Array<out ProblemTopic>
) : WeightedRandom(difficulty, random) {

    protected val easyIntEval = { Value.of(IntVar.of(randEasyInt(5, 50))) }

    protected fun genMathOperation(record: GenRecord, manipulateVar: String,
                                   opType: OperationType = RandomTypes.operationType(difficulty, random)): MathOperation {
        //TODO() better handle division and modulo divide by 0
        return if (opType != OperationType.DIVISION && opType != OperationType.MODULUS && record.hasVarType(VarType.INT_PRIMITIVE) && randHardBool())
            MathOperation(opType, manipulateVar, record.getRandVar(VarType.INT_PRIMITIVE))
        else {
            val intValue =
                    if (opType >= OperationType.MULTIPLICATION && opType < OperationType.MODULUS) randInt(1, 4)
                    else randInt(3, 20)
            MathOperation(opType, manipulateVar, IntVar.of(intValue))
        }
    }

    protected fun genIntEvaluation(record: GenRecord, vararg ignore: String): Evaluation<IntVar> = genIntEvaluation(record, easyIntEval, *ignore)

    protected fun genIntEvaluation(record: GenRecord, default: () -> Evaluation<IntVar>, vararg ignore: String): Evaluation<IntVar> {
        //if random bool and an int in record
        return if (randBool() && record.hasVarType(VarType.INT_PRIMITIVE, *ignore)) {
            //hard chance of using a math operation
            if (randHardBool()) genMathOperation(record, record.getRandVar(VarType.INT_PRIMITIVE, *ignore)!!)
            //otherwise just a simple variable
            else Variable.of(record.getRandVar(VarType.INT_PRIMITIVE, *ignore))
        } else default.invoke()
    }

}