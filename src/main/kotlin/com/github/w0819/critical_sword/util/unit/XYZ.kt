package com.github.w0819.critical_sword.util.unit

import com.github.w0819.critical_sword.util.able.Multivariable

data class XYZ(val x: Double, val y: Double, val z: Double): Multivariable<XYZ> {
    companion object {
        val defaultXYZ = XYZ(1.0, 1.0,1.0)
    }

    override fun times(n: Double): XYZ = XYZ(x * n, y * n, z * n)

    fun timesWithOutY(n: Double): XYZ = XYZ(x * n, y, z * n)

    fun timesWIthOutY(i: Int): XYZ = timesWithOutY(i.toDouble())
}