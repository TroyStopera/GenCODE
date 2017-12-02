package com.troystopera.gencode.generator

import java.util.*

class WeightedRandom(seed: Long) : Random(seed) {

    var difficulty: Double = .5
        set (value) {
            field = when {
                value > 1 -> 1.0
                value <= 0 -> 0.01
                else -> value
            }
        }

    //random boolean with default probability 50%
    fun randBool(probability: Double = 0.5): Boolean = nextDouble() > probability

    //probability more likely if difficulty high - probability never greater than 90%
    fun randHardBool() = nextDouble() < if (difficulty > .9) .9 else difficulty

    //probability more likely if difficulty low - never greater than 90% nor less than 10%
    fun randEasyBool() = nextDouble() > if (difficulty < .1) .1 else if (difficulty > .9) .9 else difficulty

    //inclusive random number
    fun randInt(min: Int = 0, max: Int = 100) = min + if (min >= max) 0 else nextInt(max - min + 1)

    fun simpleInt(): Int {
        val base = if (randBool()) 2 else 5
        return base * randInt(1, 5)
    }

    fun randEasyInt(min: Int = 0, max: Int = 100): Int {
        val easyMin = min - (min % 5)
        val easyMax = max + (max % 5)
        val div = (easyMax - easyMin) / 5
        val factor = nextInt(if (div <= 0) 1 else div)
        return easyMin + (5 * factor)
    }

}