package com.troystopera.gencode.generator

sealed class Pattern(val type: Type) {

    data class ArrayWalk(val arrayName: String, val index: String) : Pattern(Type.ARRAY_WALK)

    enum class Type {
        ARRAY_WALK
    }

}