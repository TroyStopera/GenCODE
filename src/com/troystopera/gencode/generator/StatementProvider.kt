package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import java.util.*

internal abstract class StatementProvider(
        type: ProviderType,
        difficulty: Double,
        random: Random,
        topics: Array<out ProblemTopic>) : CodeProvider(type, difficulty, random, topics) {

    abstract fun withDifficulty(difficulty: Double): StatementProvider

    abstract fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VariableProvider, record: GenRecord)

}