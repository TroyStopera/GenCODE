package com.troystopera.gencode.generator

import java.util.*

open class WeightedRandom(difficulty: Double, protected val random: Random) {

    protected val difficulty = when {
        difficulty > 1 -> 1.0
        difficulty <= 0 -> 0.01
        else -> difficulty
    }

    protected fun randBool(): Boolean = random.nextBoolean()

    //probability more likely if difficulty high - probability never greater than 90%
    protected fun randHardBool(difficulty: Double = this.difficulty): Boolean =
            random.nextDouble() < if (difficulty > .9) .9 else difficulty

    //probability more likely if difficulty low - never greater than 90% nor less than 10%
    protected fun randEasyBool(difficulty: Double = this.difficulty): Boolean =
            random.nextDouble() > if (difficulty < .1) .1 else if (difficulty > .9) .9 else difficulty

    //inclusive random number
    protected fun randInt(min: Int = 0, max: Int = 100): Int =
            min + if (min >= max) 0 else random.nextInt(max - min + 1)

    protected fun randEasyInt(min: Int = 0, max: Int = 100): Int {
        val easyMin = min - (min % 5)
        val easyMax = max + (max % 5)
        val div = (easyMax - easyMin) / 5
        val factor = random.nextInt(if (div <= 0) 1 else div)
        return easyMin + (5 * factor)
    }

}