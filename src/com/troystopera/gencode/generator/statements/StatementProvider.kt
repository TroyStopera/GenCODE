package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.jkode.Component
import com.troystopera.jkode.components.CodeBlock

internal abstract class StatementProvider(
        type: ProviderType,
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>
) : CodeProvider(type, random, topics) {

    abstract fun populate(parent: CodeBlock, varProvider: VarNameProvider, scope: GenScope, context: GenContext)

}