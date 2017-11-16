package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.`var`.IntVar
import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.ForLoop
import com.troystopera.gencode.code.statements.Assignment
import com.troystopera.gencode.code.statements.Declaration
import com.troystopera.gencode.code.statements.evaluations.*
import com.troystopera.gencode.generator.*
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.VarNameProvider
import java.util.*

internal class ForLoopProvider(
        random: WeightedRandom,
        topics: Array<out ProblemTopic>
) : ComponentProvider(ProviderType.FOR_LOOP, random, topics) {

    override fun generate(parentType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result {
        val varName = varProvider.nextVar()
        val newRecord = scope.createChildRecord()
        newRecord.addVar(varName, VarType.INT_PRIMITIVE, false)
        val up = random.randBool()
        val loop = ForLoop(genDeclaration(varName, up, newRecord), genComparison(varName, up, newRecord), genAssignment(varName, up))
        return Result(loop, arrayOf(loop), newRecord)
    }

    private fun genDeclaration(varName: String, up: Boolean, scope: GenScope): Declaration {
        return Declaration.declareWithAssign(
                varName,
                VarType.INT_PRIMITIVE,
                //TODO utilize other variables in loop declaration
                IntVar.of(if (up) 0 else random.randEasyInt(10, 20))
        )
    }

    private fun genComparison(varName: String, up: Boolean, scope: GenScope): Comparison<IntVar> {
        val type = if (up) {
            if (random.randBool()) ComparisonType.LESS_THAN else ComparisonType.LESS_THAN_EQUAL
        } else {
            if (random.randBool()) ComparisonType.GREATER_THEN else ComparisonType.GREATER_THEN_EQUAL
        }

        val value = if (up) random.randEasyInt(5, 20) else random.randEasyInt(-10, 10)
        return Comparison(type, Variable.of<IntVar>(varName), Value.of(IntVar.of(value)))

        //TODO utilize other variables in loop comparison
        /*return if (randHardBool() && scope.hasVarType(VarType.INT_PRIMITIVE)) {
            Comparison(type, Variable.of<IntVar>(varName), Variable.of<IntVar>(scope.getRandVar(VarType.INT_PRIMITIVE)))
        } else {
            val value = if (up) randEasyInt(10, 100) else randEasyInt(-20, 10)
            Comparison(type, Variable.of<IntVar>(varName), Value.of(IntVar.of(value)))
        }*/
    }

    private fun genAssignment(varName: String, up: Boolean): Assignment {
        //use multiplication or division
        return if (random.randHardBool()) {
            //TODO() figure out a workaround for multiplication for negative numbers leading to var getting smaller and smaller, currently using addition instead
            if (up) Assignment.assign(varName, MathOperation(OperationType.ADDITION, varName, IntVar.of(random.randInt(2, 3))))
            //TODO() figure out a workaround for division leading to var getting stuck at 1, currently using subtraction instead
            else Assignment.assign(varName, MathOperation(OperationType.SUBTRACTION, varName, IntVar.of(random.randInt(2, 3))))
        }
        //use addition or subtraction
        else {
            if (random.randEasyBool()) {
                if (up) Assignment.increment(varName)
                else Assignment.decrement(varName)
            } else {
                if (up) Assignment.assign(varName, MathOperation(OperationType.ADDITION, varName, IntVar.of(random.randInt(2, 5))))
                else Assignment.assign(varName, MathOperation(OperationType.SUBTRACTION, varName, IntVar.of(random.randInt(2, 5))))
            }
        }
    }

}