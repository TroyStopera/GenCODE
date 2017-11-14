package com.troystopera.gencode.generator

import com.troystopera.gencode.`var`.VarType
import java.util.*

class GenScope private constructor(
        val history: History,
        private val parent: GenScope?,
        private val depth: Int,
        private val random: Random
) {

    private val vars = hashMapOf<String, VarType>()
    private val arrayLengths = hashMapOf<String, Int>()
    private val exclude = hashSetOf<String>()

    constructor() : this(History(), null, 0, Random())

    constructor(seed: Long) : this(History(), null, 0, Random(seed))

    fun addArrVar(name: String, type: VarType, length: Int) {
        assert(!type.isPrimitive)
        vars.put(name, type)
        arrayLengths.put(name, length)
    }

    fun addVar(name: String, type: VarType, enableManipulation: Boolean = true) {
        assert(type.isPrimitive)
        vars.put(name, type)
        if (!enableManipulation) exclude.add(name)
    }

    fun getArrLength(name: String): Int {
        return arrayLengths[name] ?: 0
    }

    fun hasVar(name: String): Boolean = vars.minus(exclude).contains(name) || parent?.hasVar(name) ?: false

    fun hasVarType(type: VarType, vararg ignore: String): Boolean = vars.minus(exclude).minus(ignore).containsValue(type)
            || parent?.hasVarType(type, *ignore) ?: false

    fun getRandVar(type: VarType, vararg ignore: String): String? = getRandVar(arrayOf(type), depth, *ignore)

    fun getRandVar(type: VarType, maxDepth: Int, vararg ignore: String): String? = getRandVar(arrayOf(type), maxDepth, *ignore)

    fun getRandVar(types: Array<VarType>, vararg ignore: String): String? = getRandVar(types, depth, *ignore)

    fun getRandVar(types: Array<VarType>, maxDepth: Int, vararg ignore: String): String? {
        val all = getAllVars(types, maxDepth).minus(ignore)
        return if (all.isEmpty()) null else all.elementAt(random.nextInt(all.size))
    }

    fun getAllVars(type: VarType, maxDepth: Int = depth): Set<String> = getAllVars(arrayOf(type), maxDepth)

    fun getAllVars(types: Array<VarType>, maxDepth: Int = depth): Set<String> {
        val set = vars.keys.filter { types.contains(vars[it]) && !exclude.contains(it) }.toMutableSet()
        if (maxDepth > 0) set.addAll(parent?.getAllVars(types, maxDepth - 1) ?: emptySet())
        return set
    }

    fun createChildRecord(): GenScope = GenScope(history, this, depth + 1, random)

    class History {
        private val returnedVars = mutableSetOf<String>()

        fun addReturnedVar(name: String) = returnedVars.add(name)
        fun getReturnedVars(): Array<String> = returnedVars.toTypedArray()
    }

}