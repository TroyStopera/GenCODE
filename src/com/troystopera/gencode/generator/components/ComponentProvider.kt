package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.generator.*
import com.troystopera.jkode.Component
import com.troystopera.jkode.components.CodeBlock

internal abstract class ComponentProvider(type: ProviderType) : CodeProvider(type) {

    abstract fun generate(scope: GenScope, context: GenContext): Result

    internal companion object {

        internal fun fromTopic(topic: ProblemTopic): ComponentProvider {
            return when (topic) {
                ProblemTopic.FOR_LOOP -> ForLoopProvider
                ProblemTopic.CONDITIONAL -> ConditionalProvider
                ProblemTopic.ARRAY -> throw IllegalArgumentException("No statements for arrays")
            }
        }

        internal fun hasProvider(topic: ProblemTopic): Boolean {
            return when (topic) {
                ProblemTopic.FOR_LOOP -> true
                ProblemTopic.CONDITIONAL -> true
                ProblemTopic.ARRAY -> false
            }
        }

    }

    class Result(
            val component: Component,
            val newBlocks: Array<CodeBlock>,
            val scope: GenScope
    )

}