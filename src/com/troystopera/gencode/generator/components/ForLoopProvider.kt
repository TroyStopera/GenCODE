package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import com.troystopera.gencode.generator.constraints.ForLoopConstraints
import com.troystopera.jkode.Component
import com.troystopera.jkode.Evaluation
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.ArrayLength
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.evaluations.MathOperation
import com.troystopera.jkode.evaluations.Variable
import com.troystopera.jkode.statements.Assignment
import com.troystopera.jkode.statements.Declaration
import com.troystopera.jkode.vars.ArrayType
import com.troystopera.jkode.vars.ArrayVar
import com.troystopera.jkode.vars.IntVar
import com.troystopera.jkode.vars.VarType

internal class ForLoopProvider(
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.FOR_LOOP, random, topics) {

    override fun generate(varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val varName = varProvider.nextVar()
        val newRecord = scope.createChildRecord(ForLoop::class)
        val pattern = createPattern(varName, newRecord)
        if (pattern != null) {
            newRecord.addPattern(pattern)
            if (pattern is Pattern.ArrayWalk)
                context.mainArray = pattern.arrayName
        }
        newRecord.addVar(varName, VarType.INT, false)

        val up = ForLoopConstraints.useIncrease(random, pattern)
        val loop = ForLoop(
                genDeclaration(varName, up, newRecord, pattern),
                genComparison(varName, up, newRecord, pattern),
                genAssignment(varName, up, scope, pattern)
        )
        return Result(loop, arrayOf(loop), newRecord)
    }

    fun createPattern(intName: String, scope: GenScope): Pattern? {
        //array walk
        if (scope.hasVarType(VarType.ARRAY[VarType.INT]) && !scope.hasPattern(Pattern.ArrayWalk::class)) {
            val array = scope.getRandVar(VarType.ARRAY[VarType.INT])!!
            return Pattern.ArrayWalk(array, intName)
        }
        return null
    }

    private fun genDeclaration(varName: String, up: Boolean, scope: GenScope, pattern: Pattern?): Declaration<IntVar> {
        //TODO utilize other variables in loop declaration
        val value: Evaluation<IntVar> = when (pattern) {
        //array walk declaration
            is Pattern.ArrayWalk -> {
                if (up) IntVar[0].asEval()
                else MathOperation(MathOperation.Type.SUBTRACT, ArrayLength(Variable(VarType.ARRAY, pattern.arrayName)), IntVar[1].asEval())
            }
        //default declaration
            else -> IntVar[if (up) random.randInt(0, 2) else random.randInt(1, 5)].asEval()
        }
        return Declaration(VarType.INT, varName, value)
    }

    private fun genComparison(varName: String, up: Boolean, scope: GenScope, pattern: Pattern?): Comparison<IntVar> {
        val type: Comparison.Type = when (pattern) {
        //array walk comparison
            is Pattern.ArrayWalk -> {
                if (up) Comparison.Type.LESS_THAN
                else Comparison.Type.GREATER_THEN_EQUAL_TO
            }
        //default comparison
            else -> {
                if (up)
                    if (random.randBool()) Comparison.Type.LESS_THAN else Comparison.Type.LESS_THAN_EQUAL_TO
                else
                    if (random.randBool()) Comparison.Type.GREATER_THAN else Comparison.Type.GREATER_THEN_EQUAL_TO
            }
        }

        val value: Evaluation<IntVar> = when (pattern) {
        //array walk values
            is Pattern.ArrayWalk -> {
                if (up) ArrayLength(Variable(VarType.ARRAY, pattern.arrayName))
                else IntVar[0].asEval()
            }
        //default value
            else -> IntVar[if (up) random.randInt(1, 5) else random.randInt(0, 3)].asEval()
        }
        return Comparison(type, Variable(VarType.INT, varName), value)
    }

    private fun genAssignment(varName: String, up: Boolean, scope: GenScope, pattern: Pattern?): Assignment {
        val varVariable = Variable(VarType.INT, varName)
        return when {
            pattern is Pattern.ArrayWalk -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by 1
            ForLoopConstraints.useSingleStep(random, scope) -> {
                if (up) Assignment(varName, MathOperation(MathOperation.Type.ADD, varVariable, IntVar[1].asEval()))
                else Assignment(varName, MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[1].asEval()))
            }
        //add or subtract by a random number
            else -> {
                if (up) Assignment(
                        varName,
                        MathOperation(MathOperation.Type.ADD, varVariable, IntVar[random.randInt(1, 2)].asEval())
                )
                else Assignment(
                        varName,
                        MathOperation(MathOperation.Type.SUBTRACT, varVariable, IntVar[random.randInt(1, 2)].asEval())
                )
            }
        }
    }

}