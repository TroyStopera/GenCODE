package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.ForLoop
import com.troystopera.gencode.code.statements.Assignment
import com.troystopera.gencode.code.statements.Declaration
import com.troystopera.gencode.code.statements.Evaluation
import com.troystopera.gencode.code.statements.evaluations.*
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider

internal class ForLoopProvider(
        random: WeightedRandom,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.FOR_LOOP, random, topics) {

    override fun generate(parentType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val varName = varProvider.nextVar()
        val newRecord = scope.createChildRecord(ForLoop::class)
        val pattern = createPattern(varName, newRecord)
        if (pattern != null) {
            newRecord.addPattern(pattern)
            if (pattern is Pattern.ArrayWalk)
                context.mainArray = pattern.arrayName
        }
        newRecord.addVar(varName, VarType.INT_PRIMITIVE, false)

        val up = random.randBool()
        val loop = ForLoop(
                genDeclaration(varName, up, newRecord, pattern),
                genComparison(varName, up, newRecord, pattern),
                genAssignment(varName, up, pattern)
        )
        return Result(loop, arrayOf(loop), newRecord)
    }

    fun createPattern(intName: String, scope: GenScope): Pattern? {
        //array walk
        if (scope.hasVarType(VarType.INT_ARRAY) && !scope.hasPattern(Pattern.Type.ARRAY_WALK)) {
            val array = scope.getRandVar(VarType.INT_ARRAY)!!
            return Pattern.ArrayWalk(array, intName)
        }
        return null
    }

    private fun genDeclaration(varName: String, up: Boolean, scope: GenScope, pattern: Pattern?): Declaration {
        //TODO utilize other variables in loop declaration
        val value: Evaluation<*> = when (pattern) {
        //array walk declaration
            is Pattern.ArrayWalk -> {
                if (up) Value.of(IntVar.of(0))
                else MathOperation(OperationType.SUBTRACTION, ArrayLength.of(pattern.arrayName), Value.of(IntVar.of(1)))
            }
        //default declaration
            else -> Value.of(IntVar.of(if (up) 0 else random.randEasyInt(5, 10)))
        }
        return Declaration.declareWithAssign(varName, VarType.INT_PRIMITIVE, value)
    }

    private fun genComparison(varName: String, up: Boolean, scope: GenScope, pattern: Pattern?): Comparison<IntVar> {
        val type: ComparisonType = when (pattern) {
        //array walk comparison
            is Pattern.ArrayWalk -> {
                if (up) ComparisonType.LESS_THAN
                else ComparisonType.GREATER_THEN_EQUAL
            }
        //default comparison
            else -> {
                if (up)
                    if (random.randBool()) ComparisonType.LESS_THAN else ComparisonType.LESS_THAN_EQUAL
                else
                    if (random.randBool()) ComparisonType.GREATER_THEN else ComparisonType.GREATER_THEN_EQUAL
            }
        }

        val value: Evaluation<IntVar> = when (pattern) {
        //array walk values
            is Pattern.ArrayWalk -> {
                if (up) ArrayLength.of(pattern.arrayName)
                else Value.of(IntVar.of(0))
            }
        //default value
            else -> Value.of(IntVar.of(if (up) random.randInt(5, 10) else random.randInt(0, 5)))
        }
        return Comparison(type, Variable.of<IntVar>(varName), value)

        //TODO utilize other variables in loop comparison
        /*return if (randHardBool() && scope.hasVarType(VarType.INT_PRIMITIVE)) {
            Comparison(type, Variable.of<IntVar>(varName), Variable.of<IntVar>(scope.getRandVar(VarType.INT_PRIMITIVE)))
        } else {
            val value = if (up) randEasyInt(10, 100) else randEasyInt(-20, 10)
            Comparison(type, Variable.of<IntVar>(varName), Value.of(IntVar.of(value)))
        }*/
    }

    private fun genAssignment(varName: String, up: Boolean, pattern: Pattern?): Assignment {
        return when {
            pattern is Pattern.ArrayWalk -> {
                if (up) Assignment.increment(varName)
                else Assignment.decrement(varName)
            }
        //multiply or divide
            random.randHardBool() -> {
                //TODO() figure out a workaround for multiplication for negative numbers leading to var getting smaller and smaller, currently using addition instead
                if (up) Assignment.assign(
                        varName,
                        MathOperation(OperationType.ADDITION, varName, IntVar.of(random.randInt(2, 2)))
                )
                //TODO() figure out a workaround for division leading to var getting stuck at 1, currently using subtraction instead
                else Assignment.assign(
                        varName,
                        MathOperation(OperationType.SUBTRACTION, varName, IntVar.of(random.randInt(2, 2)))
                )
            }
        //add or subtract by 1
            random.randEasyBool() -> {
                if (up) Assignment.increment(varName)
                else Assignment.decrement(varName)
            }
        //add or subtract by a random number
            else -> {
                if (up) Assignment.assign(
                        varName,
                        MathOperation(OperationType.ADDITION, varName, IntVar.of(random.randInt(2, 3)))
                )
                else Assignment.assign(
                        varName,
                        MathOperation(OperationType.SUBTRACTION, varName, IntVar.of(random.randInt(2, 3)))
                )
            }
        }
    }

}