package com.troystopera.gencode.generator.constraints

import com.troystopera.gencode.generator.DifficultyRandom

object ManipulationConstraints {

    const val LEVEL_0 = 0.00
    const val LEVEL_1 = 0.25
    const val LEVEL_2 = 0.50
    const val LEVEL_3 = 0.75

    fun useDirectManipulation(random: DifficultyRandom) = random.difficulty < LEVEL_1

}