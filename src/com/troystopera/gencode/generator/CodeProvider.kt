package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import com.troystopera.jkode.Component
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal abstract class CodeProvider(
        val type: ProviderType,
        protected val random: DifficultyRandom,
        protected val topics: Array<out ProblemTopic>
) {

    protected val easyIntEval = { IntVar[random.randEasyInt(5, 50)].asEval() }

    protected fun genMathOperation(scope: GenScope, manipulateVar: String, opType: MathOperation.Type): MathOperation<Int, IntVar> {
        //TODO() better handle division and modulo divide by 0
        return if (opType != MathOperation.Type.DIVIDE && opType != MathOperation.Type.MODULO && scope.hasVarType(VarType.INT) && random.randHardBool())
            MathOperation(opType, Variable(VarType.INT, manipulateVar), Variable(VarType.INT, scope.getRandVar(VarType.INT)!!))
        else {
            val intValue =
                    if (opType >= MathOperation.Type.MULTIPLY && opType < MathOperation.Type.MODULO) random.randInt(1, 4)
                    else random.randInt(3, 20)
            MathOperation(opType, Variable(VarType.INT, manipulateVar), IntVar[intValue].asEval())
        }
    }

    protected fun genIntEvaluation(scope: GenScope, vararg ignore: String): Evaluation<IntVar> = genIntEvaluation(scope, easyIntEval, *ignore)

    protected fun genIntEvaluation(scope: GenScope, default: () -> Evaluation<IntVar>, vararg ignore: String): Evaluation<IntVar> {
        //if random bool and an int in record
        return if (random.randBool() && scope.hasVarType(VarType.INT, *ignore)) {
            //hard chance of using a math operation
            if (random.randHardBool()) genMathOperation(scope, scope.getRandVar(VarType.INT, *ignore)!!, RandomTypes.operationType(random.difficulty, random))
            //otherwise just a simple variable
            else Variable(VarType.INT, scope.getRandVar(VarType.INT, *ignore)!!)
        } else default.invoke()
    }

    class Result(
            val component: Component,
            val newBlocks: Array<CodeBlock>,
            val scope: GenScope
    )

}