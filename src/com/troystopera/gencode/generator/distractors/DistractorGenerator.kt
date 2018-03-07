package com.troystopera.gencode.generator.distractors

import com.troystopera.gencode.Problem
import com.troystopera.gencode.ProblemTopic
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.evaluations.Comparison
import com.troystopera.jkode.exec.Executor
import com.troystopera.jkode.exec.override.OverrideExecutor
import com.troystopera.jkode.exec.override.Watchable
import com.troystopera.jkode.vars.*
import java.util.*

class DistractorGenerator(private val problem: Problem) {

    private val random = Random()
    private val topics = problem.topics.toHashSet()
    lateinit var overrideExec: OverrideExecutor

    fun getDistractors(count: Int) = genDistractors(count, problem.mainFunction.returnType.NULL)

    private inline fun <reified T : JVar<*>> genDistractors(count: Int, nullVar: T): List<String> {
        val distractors = mutableSetOf<String>()
        val correct = Executor().execute(problem.mainFunction).getReturnVar() as? T ?: nullVar

        //try to override loops first
        if (topics.contains(ProblemTopic.FOR_LOOP)) {
            overrideExec = OverrideExecutor()
            overrideExec.addOverride(Watchable(ForLoop::class), ForLoopOverride.SkipFirst)
            val result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T ?: nullVar
            if (result.toString() != correct.toString())
                distractors.add(result.toString())
        }

        //now try comparison overrides

        overrideExec = OverrideExecutor()
        overrideExec.addOverride(Watchable(Comparison::class), ComparisonOverride.InvertedBoolean)
        var result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T ?: nullVar
        if (result.toString() != correct.toString())
            distractors.add(result.toString())

        overrideExec = OverrideExecutor()
        overrideExec.addOverride(Watchable(Comparison::class), ComparisonOverride.OrEqualToMistake)
        result = overrideExec.execute(problem.mainFunction).getReturnVar() as? T ?: nullVar
        if (result.toString() != correct.toString())
            distractors.add(result.toString())

        //ensure the number of distractors is sufficient
        if (distractors.size < count) when (correct) {
            is IntVar -> fillList(count, correct, distractors)
            else -> TODO("take into consideration return types other than int")
        }

        //ensure the number of distractors is not too high
        return distractors.take(count)
    }

    private fun fillList(count: Int, correct: IntVar, currentDistractors: MutableSet<String>) {
        val strings = mutableListOf<String>()
        var delta = 1 + random.nextInt(3)

        while (currentDistractors.size < count) {
            strings.add((count + delta).toString())
            delta += 1 + random.nextInt(3)
            delta *= -1
        }
    }

}