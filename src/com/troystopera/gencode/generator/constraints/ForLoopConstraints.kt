package com.troystopera.gencode.generator.constraints

import com.troystopera.gencode.generator.DifficultyRandom
import com.troystopera.gencode.generator.GenScope
import com.troystopera.gencode.generator.NestStructure
import com.troystopera.gencode.generator.Pattern

object ForLoopConstraints {

    const val LEVEL_0 = 0.00
    const val LEVEL_1 = 0.25
    const val LEVEL_2 = 0.50
    const val LEVEL_3 = 0.75

    fun useIncrease(random: DifficultyRandom, pattern: Pattern?) =
            if (pattern is Pattern.ArrayWalk) random.difficulty < LEVEL_1 || random.nextBoolean()
            else random.nextBoolean()

    fun useSingleStep(random: DifficultyRandom, scope: GenScope): Boolean {
        val pattern = scope.getPattern(Pattern.NestPattern::class) as? Pattern.NestPattern
        return when {
            random.difficulty < LEVEL_1 -> true
            random.difficulty < LEVEL_2 -> {
                when (pattern) {
                    is NestStructure.NestedLoop -> true
                    else -> random.randBool(0.4)
                }
            }
            else -> random.randBool(0.2)
        }
    }

}