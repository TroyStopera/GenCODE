package com.troystopera.gencode.generator

import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal abstract class CodeProvider(val type: ProviderType) {

    protected fun genMathOperation(context: GenContext, scope: GenScope, manipulateVar: String, opType: MathOperation.Type): MathOperation<Int, IntVar> {
        //TODO() better handle division and modulo divide by 0
        return if (opType != MathOperation.Type.DIVIDE && opType != MathOperation.Type.MODULO && scope.hasVarType(VarType.INT) && context.random.randHardBool())
            MathOperation(opType, Variable(VarType.INT, manipulateVar), Variable(VarType.INT, scope.getRandVar(VarType.INT)!!))
        else {
            val intValue =
                    if (opType >= MathOperation.Type.MULTIPLY && opType < MathOperation.Type.MODULO) context.random.randInt(1, 4)
                    else context.random.randInt(3, 20)
            MathOperation(opType, Variable(VarType.INT, manipulateVar), IntVar[intValue].asEval())
        }
    }

    protected fun genIntEvaluation(context: GenContext, scope: GenScope, vararg ignore: String): Evaluation<IntVar> =
            genIntEvaluation(context, scope, { IntVar[context.random.randEasyInt(5, 50)].asEval() }, *ignore)

    protected fun genIntEvaluation(context: GenContext, scope: GenScope, default: () -> Evaluation<IntVar>, vararg ignore: String): Evaluation<IntVar> {
        //if random bool and an int in record
        return if (context.random.randBool() && scope.hasVarType(VarType.INT, *ignore)) {
            //hard chance of using a math operation
            if (context.random.randHardBool()) genMathOperation(context, scope, scope.getRandVar(VarType.INT, *ignore)!!, RandomTypes.operationType(context.random.difficulty, context.random))
            //otherwise just a simple variable
            else Variable(VarType.INT, scope.getRandVar(VarType.INT, *ignore)!!)
        } else default.invoke()
    }

}