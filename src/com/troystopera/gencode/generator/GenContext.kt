package com.troystopera.gencode.generator

class GenContext {

    var mainIntVar: String? = null
    var mainArray: String? = null

    private val returnedVars: MutableSet<String> = mutableSetOf()

    fun registerReturnedVar(name: String) {
        returnedVars.add(name)
    }

}