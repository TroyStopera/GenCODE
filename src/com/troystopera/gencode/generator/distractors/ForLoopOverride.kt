package com.troystopera.gencode.generator.distractors

import com.troystopera.jkode.CtrlStmt
import com.troystopera.jkode.components.CodeBlock
import com.troystopera.jkode.components.ForLoop
import com.troystopera.jkode.control.Break
import com.troystopera.jkode.control.Return
import com.troystopera.jkode.exec.Executor
import com.troystopera.jkode.exec.MutableOutput
import com.troystopera.jkode.exec.Scope
import com.troystopera.jkode.exec.override.ExecutionOverride
import java.util.*

sealed class ForLoopOverride : ExecutionOverride<CtrlStmt<*>?, ForLoop> {

    object SkipFirst : ForLoopOverride() {

        override fun execute(executable: ForLoop, scope: Scope, executor: Executor, output: MutableOutput?): CtrlStmt<*>? {
            //run the for loop's initialization
            executable.initialization.execute(scope, executor, output)
            //run the afterthought first before running the body
            executable.afterthought.execute(scope, executor, output)
            //run the for loop like usual from here
            while (executable.condition.execute(scope, executor, output).value) {
                val v = executable.executeBody(scope.newChildScope(), executor, output)
                when (v) {
                    Break -> return null
                    is Return<*> -> return v
                }
                executable.afterthought.execute(scope, executor, output)
            }
            return null
        }

    }

}