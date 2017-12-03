package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.code.statements.Evaluation
import com.troystopera.gencode.code.statements.evaluations.MathOperation
import com.troystopera.gencode.code.statements.evaluations.OperationType
import com.troystopera.gencode.code.statements.evaluations.Value
import com.troystopera.gencode.code.statements.evaluations.Variable

internal abstract class CodeProvider(
        val type: ProviderType,
        protected val random: DifficultyRandom,
        protected val topics: Array<out ProblemTopic>
) {

    protected val easyIntEval = { Value.of(IntVar.of(random.randEasyInt(5, 50))) }

    protected fun genMathOperation(scope: GenScope, manipulateVar: String, opType: OperationType): MathOperation {
        //TODO() better handle division and modulo divide by 0
        return if (opType != OperationType.DIVISION && opType != OperationType.MODULUS && scope.hasVarType(VarType.INT_PRIMITIVE) && random.randHardBool())
            MathOperation(opType, manipulateVar, scope.getRandVar(VarType.INT_PRIMITIVE))
        else {
            val intValue =
                    if (opType >= OperationType.MULTIPLICATION && opType < OperationType.MODULUS) random.randInt(1, 4)
                    else random.randInt(3, 20)
            MathOperation(opType, manipulateVar, IntVar.of(intValue))
        }
    }

    protected fun genIntEvaluation(scope: GenScope, vararg ignore: String): Evaluation<IntVar> = genIntEvaluation(scope, easyIntEval, *ignore)

    protected fun genIntEvaluation(scope: GenScope, default: () -> Evaluation<IntVar>, vararg ignore: String): Evaluation<IntVar> {
        //if random bool and an int in record
        return if (random.randBool() && scope.hasVarType(VarType.INT_PRIMITIVE, *ignore)) {
            //hard chance of using a math operation
            if (random.randHardBool()) genMathOperation(scope, scope.getRandVar(VarType.INT_PRIMITIVE, *ignore)!!, RandomTypes.operationType(random.difficulty, random))
            //otherwise just a simple variable
            else Variable.of(scope.getRandVar(VarType.INT_PRIMITIVE, *ignore))
        } else default.invoke()
    }

    class Result(
            val component: Component,
            val newBlocks: Array<CodeBlock>,
            val scope: GenScope
    )

}