package com.troystopera.gencode.generator.distractors

import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.exec.Executor
import com.troystopera.jkode.exec.MutableOutput
import com.troystopera.jkode.exec.Scope
import com.troystopera.jkode.exec.override.ExecutionOverride
import com.troystopera.jkode.vars.BooleanVar

sealed class ComparisonOverride : ExecutionOverride<BooleanVar, Comparison<*>> {

    object InvertedBoolean : ComparisonOverride() {

        override fun execute(executable: Comparison<*>, scope: Scope, output: MutableOutput?, executor: Executor): BooleanVar {
            val value = executable.execute(scope, output).value
            return BooleanVar[!value]
        }

    }

    object OrEqualToMistake : ComparisonOverride() {

        override fun execute(executable: Comparison<*>, scope: Scope, output: MutableOutput?, executor: Executor): BooleanVar {
            val comparison = when (executable.type) {
                Comparison.Type.LESS_THAN -> Comparison.Type.LESS_THAN_EQUAL_TO
                Comparison.Type.LESS_THAN_EQUAL_TO -> Comparison.Type.LESS_THAN
                Comparison.Type.EQUAL_TO -> Comparison.Type.EQUAL_TO
                Comparison.Type.NOT_EQUAL_TO -> Comparison.Type.NOT_EQUAL_TO
                Comparison.Type.GREATER_THAN_EQUAL_TO -> Comparison.Type.GREATER_THAN
                Comparison.Type.GREATER_THAN -> Comparison.Type.GREATER_THAN_EQUAL_TO
            }
            return executable.withCompType(comparison).execute(scope, output)
        }

    }

}