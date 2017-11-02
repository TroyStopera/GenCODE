package com.troystopera.gencode.generator

import com.troystopera.gencode.code.statements.evaluations.ComparisonType
import com.troystopera.gencode.code.statements.evaluations.OperationType
import java.util.*

object RandomTypes {

    fun operationType(difficulty: Double, random: Random): OperationType {
        val maxIndex = when {
            difficulty >= THRESHOLD_OP_TYPE_MOD -> 5
            difficulty >= THRESHOLD_OP_TYPE_MULT_DIV -> 4
            else -> 2
        }
        return OperationType.values()[random.nextInt(maxIndex)]
    }

    fun comparisonType(random: Random): ComparisonType {
        val values = ComparisonType.values()
        return values[random.nextInt(values.size)]
    }

}