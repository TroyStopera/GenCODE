package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic

class GenContext(
        val random: DifficultyRandom,
        val topics: List<ProblemTopic>,
        val variableProvider: VarNameProvider
) {

    var mainIntVar: String? = null
    var mainArray: String? = null

}