package com.github.w0819.critical_sword.util.unit

import org.bukkit.configuration.serialization.ConfigurationSerializable

data class Radios(val x: Double, val y: Double, val z: Double): ConfigurationSerializable {
    override fun serialize(): MutableMap<String, Any> = mutableMapOf(
        "x-length" to x,
        "y-length" to y,
        "z-length" to z
    )

    operator fun times(i: Int): Radios = Radios(x * i, y * i, z * i)
    operator fun times(d: Double): Radios = Radios(x * d, y * d, z * d)

}