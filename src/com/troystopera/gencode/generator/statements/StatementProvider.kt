package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.code.components.CodeBlock
import com.troystopera.gencode.generator.*

internal abstract class StatementProvider(
        type: ProviderType,
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>) : CodeProvider(type, random, topics) {

    abstract fun populate(parent: CodeBlock, parentCompType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext)

}