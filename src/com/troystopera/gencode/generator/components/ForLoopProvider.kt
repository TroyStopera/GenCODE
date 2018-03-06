package com.troystopera.gencode.generator.components

import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.constraints.ForLoopConstraints
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.ArrayLength
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.statements.Assignment
import com.troystopera.jkode.statements.Declaration
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal object ForLoopProvider : ComponentProvider(ProviderType.FOR_LOOP) {

    override fun generate(scope: GenScope, context: GenContext): Result {
        val varName = context.variableProvider.nextVar()
        val newScope = scope.createChildScope(ForLoop::class)
        val pattern = createPattern(varName, newScope)
        if (pattern != null) {
            newScope.addPattern(pattern)
            if (pattern is Pattern.ArrayWalk)
                context.mainArray = pattern.arrayName
        }
        newScope.addVar(varName, VarType.INT, false)

        val up = ForLoopConstraints.useIncrease(context.random, pattern)
        val loop = ForLoop(
                genDeclaration(varName, up, context, pattern),
                genComparison(varName, up, context, pattern),
                genAssignment(varName, up, context, scope, pattern)
        )
        return Result(loop, arrayOf(loop), newScope)
    }

    fun createPattern(intName: String, scope: GenScope): Pattern? {
        //array walk
        if (scope.hasVarType(VarType.ARRAY[VarType.INT]) && !scope.hasPattern(Pattern.ArrayWalk::class)) {
            val array = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
            return Pattern.ArrayWalk(array, intName)
        }
        return null
    }

    private fun genDeclaration(varName: String, up: Boolean, context: GenContext, pattern: Pattern?): Declaration<IntVar> {
        //TODO utilize other variables in loop declaration
        val value: Evaluation<IntVar> = when (pattern) {
        //array walk declaration
            is Pattern.ArrayWalk -> {
                if (up) IntVar[0].asEval()
                else MathOperation(MathOperation.Type.SUBTRACT, ArrayLength(Variable(VarType.ARRAY, pattern.arrayName)), IntVar[1].asEval())
            }
        //default declaration
            else -> IntVar[if (up) context.random.randInt(0, 2) else context.random.randInt(1, 5)].asEval()
        }
        return Declaration(VarType.INT, varName, value)
    }

    private fun genComparison(varName: String, up: Boolean, context: GenContext, pattern: Pattern?): Comparison<IntVar> {
        val type: Comparison.Type = when (pattern) {
        //array walk comparison
            is Pattern.ArrayWalk -> {
                if (up) Comparison.Type.LESS_THAN
                else Comparison.Type.GREATER_THAN_EQUAL_TO
            }
        //default comparison
            else -> {
                if (up)
                    if (context.random.randBool()) Comparison.Type.LESS_THAN else Comparison.Type.LESS_THAN_EQUAL_TO
                else
                    if (context.random.randBool()) Comparison.Type.GREATER_THAN else Comparison.Type.GREATER_THAN_EQUAL_TO
            }
        }

        val value: Evaluation<IntVar> = when (pattern) {
        //array walk values
            is Pattern.ArrayWalk -> {
                if (up) ArrayLength(Variable(VarType.ARRAY, pattern.arrayName))
                else IntVar[0].asEval()
            }
        //default value
            else -> IntVar[if (up) context.random.randInt(1, 5) else context.random.randInt(0, 3)].asEval()
        }
        return Comparison(type, Variable(VarType.INT, varName), value)
    }

    private fun genAssignment(varName: String, up: Boolean, context: GenContext, scope: GenScope, pattern: Pattern?): Assignment {
        val varVariable = Variable(VarType.INT, varName)
        return when {
            pattern is Pattern.ArrayWalk -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by 1
            ForLoopConstraints.useSingleStep(context.random, scope) -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by a random number
            else -> {
                if (up) Assignment(
                        varName,
                        MathOperation(MathOperation.Type.ADD, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
                )
                else Assignment(
                        varName,
                        MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[context.random.randInt(1, 2)].asEval())
                )
            }
        }
    }

}