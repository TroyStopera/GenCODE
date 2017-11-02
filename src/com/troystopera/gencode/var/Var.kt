package com.troystopera.gencode.`var`

import java.util.*

abstract class Var(val type: VarType) {

    companion object {
        fun random(type: VarType, random: Random): Var {
            return when (type) {
                VarType.INT_ARRAY -> {
                    val ints = Array<IntVar>(1 + random.nextInt(10), { IntVar.random(random, 100) })
                    ArrayVar.of(*ints)
                }
                VarType.BOOLEAN_ARRAY -> {
                    val ints = Array<BooleanVar>(1 + random.nextInt(10), { BooleanVar.of(random.nextBoolean()) })
                    ArrayVar.of(*ints)
                }
                VarType.STRING_ARRAY -> {
                    val ints = Array<StringVar>(1 + random.nextInt(10), { StringVar.of("hello_" + it) })
                    ArrayVar.of(*ints)
                }
                VarType.INT_PRIMITIVE -> IntVar.random(random, 100)
                VarType.BOOLEAN_PRIMITIVE -> BooleanVar.of(random.nextBoolean())
                VarType.STRING_PRIMITIVE -> StringVar.of("hello_" + random.nextInt(100))
            }
        }
    }

}