package com.troystopera.gencode.generator.constraints

import com.troystopera.gencode.generator.DifficultyRandom
import com.troystopera.gencode.generator.Pattern

object ForLoopConstraints {

    const val LEVEL_0 = 0.00
    const val LEVEL_1 = 0.25
    const val LEVEL_2 = 0.50
    const val LEVEL_3 = 0.75

    fun useIncrease(random: DifficultyRandom, pattern: Pattern?) =
            if (pattern is Pattern.ArrayWalk) random.difficulty < LEVEL_1 || random.nextBoolean()
            else random.nextBoolean()

}