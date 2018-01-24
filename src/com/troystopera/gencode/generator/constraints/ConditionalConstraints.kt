package com.troystopera.gencode.generator.constraints

import com.troystopera.gencode.generator.DifficultyRandom
import com.troystopera.jkode.evaluations.Comparison

object ConditionalConstraints {

    const val LEVEL_0 = 0.00
    const val LEVEL_1 = 0.25
    const val LEVEL_2 = 0.50
    const val LEVEL_3 = 0.75

    internal const val MIN_BRANCHES = 2
    internal const val MAX_BRANCHES = 5

    private val compOps0 = arrayOf(Comparison.Type.LESS_THAN, Comparison.Type.GREATER_THAN, Comparison.Type.EQUAL_TO)
    private val compOps1 = Comparison.Type.values()

    fun useExpression(random: DifficultyRandom) =
            random.difficulty >= LEVEL_1 && random.randBool(if (random.difficulty > .8) .8 else random.difficulty)

    fun useSelfCompare(random: DifficultyRandom, compType: Comparison.Type) =
            random.difficulty < LEVEL_3 && (compType == Comparison.Type.LESS_THAN || compType == Comparison.Type.GREATER_THAN) && random.randBool(0.2)

    fun useIntLiteral(random: DifficultyRandom) = random.difficulty < LEVEL_1 && random.randEasyBool()

    fun useElse(random: DifficultyRandom) = random.randHardBool(0.33)

    fun getBranchCount(random: DifficultyRandom) = MIN_BRANCHES + when {
        random.difficulty > LEVEL_3 -> random.weightedHighNumber(0, MAX_BRANCHES - MIN_BRANCHES)
        random.difficulty > LEVEL_2 -> random.weightedHighNumber(0, MAX_BRANCHES - MIN_BRANCHES - 1)
        random.difficulty > LEVEL_1 -> random.weightedHighNumber(0, MAX_BRANCHES - MIN_BRANCHES - 2)
        else -> 0
    }

    fun getCompType(random: DifficultyRandom): Comparison.Type {
        val options = if (random.difficulty < LEVEL_1) compOps0 else compOps1
        return options[random.nextInt(options.size)]
    }

}