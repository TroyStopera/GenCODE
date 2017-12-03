package com.troystopera.gencode.generator.components

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.generator.*

internal abstract class ComponentProvider(
        type: ProviderType,
        random: DifficultyRandom,
        topics: Array<out ProblemTopic>) : CodeProvider(type, random, topics) {

    abstract fun generate(parentType: Component.Type, varProvider: VarNameProvider, scope: GenScope, context: GenContext): Result

    internal companion object {

        internal fun fromTopic(topic: ProblemTopic, topics: Array<out ProblemTopic>, random: DifficultyRandom): ComponentProvider {
            return when (topic) {
                ProblemTopic.FOR_LOOP -> ForLoopProvider(random, topics)
                ProblemTopic.CONDITIONAL -> ConditionalProvider(random, topics)
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

}