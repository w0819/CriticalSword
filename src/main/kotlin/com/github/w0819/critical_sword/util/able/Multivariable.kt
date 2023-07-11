package com.github.w0819.critical_sword.util.able

interface Multivariable<T> {
    operator fun times(n: Double): T

    operator fun times(i: Int): T = times(i.toDouble())
}