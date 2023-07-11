package com.github.w0819.critical_sword.util.unit

class HowFit<T,U> private constructor(
    val value: U, val sizeToFit: Int, private vararg val isFit: (T) -> Boolean
) {

    val isAllFit :(List<T>) -> Boolean = { acc -> isFit.all { acc.find(it) != null } }

    companion object {
        operator fun <T, U> invoke(value: U, vararg options: (T) -> Boolean): HowFit<T, U> = HowFit(value, options.size, *options)
    }
}