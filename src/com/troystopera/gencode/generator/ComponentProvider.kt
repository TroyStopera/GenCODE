package com.troystopera.gencode.generator

import com.troystopera.gencode.ProblemTopic
import com.troystopera.gencode.code.Component
import com.troystopera.gencode.generator.provider.ConditionalProvider
import com.troystopera.gencode.generator.provider.ForLoopProvider
import java.util.*

internal abstract class ComponentProvider(
        type: ProviderType,
        difficulty: Double,
        random: Random,
        topics: Array<out ProblemTopic>) : CodeProvider(type, difficulty, random, topics) {

    abstract fun withDifficulty(difficulty: Double): ComponentProvider

    abstract fun generate(parentType: Component.Type, varProvider: VariableProvider, record: GenRecord): ProviderResult

    internal companion object {

        internal fun fromTopic(topic: ProblemTopic, difficulty: Double, topics: Array<out ProblemTopic>, seed: Long = Random().nextLong()): ComponentProvider {
            return when (topic) {
                ProblemTopic.FOR_LOOP -> ForLoopProvider(difficulty, seed, topics)
                ProblemTopic.CONDITIONAL -> ConditionalProvider(difficulty, seed, topics)
                ProblemTopic.ARRAY -> throw IllegalArgumentException("No provider for arrays")
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