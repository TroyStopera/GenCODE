package com.troystopera.gencode.exec

import com.troystopera.gencode.GenerationException
import com.troystopera.gencode.`var`.ArrayVar
import com.troystopera.gencode.`var`.Var
import com.troystopera.gencode.code.components.Function

class Scope protected constructor(val parent: Scope?, val depth: Int) {

    protected val functions = hashMapOf<String, Function>()
    protected val vars = hashMapOf<String, Var?>()

    constructor() : this(null, 0)

    fun getParent(up: Int = 1): Scope {
        var scope = this
        for (i in 1..up)
            if (scope.parent != null)
                scope = scope.getParent()
            else break
        return scope
    }

    fun hasVar(name: String): Boolean = vars.containsKey(name) || parent?.hasVar(name) ?: false

    fun addVar(name: String, v: Var? = null) = vars.put(name, v)

    fun getVar(name: String): Var {
        return vars[name] ?:
                parent?.getVar(name)
                ?: throw GenerationException(NoSuchElementException("Unknown variable: $name"))
    }

    fun setVar(name: String, v: Var) {
        if (vars.containsKey(name)) vars.put(name, v)
        else parent?.setVar(name, v) ?: throw GenerationException(NullPointerException("Unknown variable $name"))
    }

    @Suppress("UNCHECKED_CAST")
    fun getArrVar(name: String, index: Int): Var {
        val arr = getVar(name) as? ArrayVar<*> ?: throw GenerationException(IllegalArgumentException("Non array var: $name"))
        if (arr.array.size <= index) throw GenerationException(ArrayIndexOutOfBoundsException("Index $index out of bounds"))
        return arr.array[index]
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Var> setArrVar(name: String, index: Int, v: T) {
        val arr = getVar(name) as? ArrayVar<T> ?: throw GenerationException(IllegalArgumentException("Non array var: $name"))
        if (arr.array.size <= index) throw GenerationException(ArrayIndexOutOfBoundsException("Index $index out of bounds"))
        arr.array[index] = v
    }

    fun nullVar(name: String) = vars.put(name, null)

    @Suppress("UNCHECKED_CAST")
    fun nullArrVar(name: String, index: Int) {
        val arr = getVar(name) as? ArrayVar<Var> ?: throw GenerationException(IllegalArgumentException("Non array var: $name"))
        if (arr.array.size <= index) throw GenerationException(ArrayIndexOutOfBoundsException("Index $index out of bounds"))
        arr.array[index] = null
    }

    fun hasFun(name: String): Boolean = functions.containsKey(name) || (parent?.hasFun(name) ?: false)

    fun addFun(name: String, f: Function) = functions.put(name, f)

    fun getFun(name: String): Function = functions[name] ?: throw GenerationException(NoSuchElementException(name))

    open fun createChildScope(): Scope = Scope(this, depth + 1)

}