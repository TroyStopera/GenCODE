package com.troystopera.gencode.generator

import com.troystopera.gencode.`var`.VarType
import com.troystopera.gencode.code.Component
import java.util.*
import kotlin.reflect.KClass

class GenScope private constructor(
        val history: History,
        private val compClass: KClass<out Component>?,
        private val parent: GenScope?,
        private val depth: Int,
        private val random: Random
) {

    private val vars = hashMapOf<String, VarType>()
    private val arrayLengths = hashMapOf<String, Int>()
    private val exclude = hashSetOf<String>()

    constructor(random: Random) : this(History(), null, null, 0, random)

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

    fun getRandUnmanipVar(type: VarType, vararg ignore: String): String? {
        val all = vars.keys.filter { vars[it] == type }.toMutableSet()
        var p = parent
        while (p != null) {
            val v = p.vars
            all.addAll(v.keys.filter { v[it] == type })
            p = p.parent
        }

        val allEx = exclude.toMutableSet()
        p = parent
        while (p != null) {
            allEx.addAll(p.exclude)
            p = p.parent
        }

        val avail = all.intersect(allEx).minus(ignore)
        return if (avail.isEmpty()) null else avail.elementAt(random.nextInt(avail.size))
    }

    fun getAllVars(type: VarType, maxDepth: Int = depth, includeAll: Boolean = false): Set<String> = getAllVars(arrayOf(type), maxDepth, includeAll)

    fun getAllVars(types: Array<VarType>, maxDepth: Int = depth, includeAll: Boolean = false): Set<String> {
        val set = vars.keys.filter { types.contains(vars[it]) && (includeAll || !exclude.contains(it)) }.toMutableSet()
        if (maxDepth > 0) set.addAll(parent?.getAllVars(types, maxDepth - 1, includeAll) ?: emptySet())
        return set
    }

    fun <T : Component> isIn(kClass: KClass<T>): Boolean =
            compClass?.equals(kClass) ?: false || parent?.isIn(kClass) ?: false

    fun <T : Component> createChildRecord(kClass: KClass<T>): GenScope = GenScope(history, kClass, this, depth + 1, random)

    class History {
        private val returnedVars = mutableSetOf<String>()

        fun addReturnedVar(name: String) = returnedVars.add(name)
        fun getReturnedVars(): Array<String> = returnedVars.toTypedArray()
    }

}