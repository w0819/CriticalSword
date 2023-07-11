package com.github.w0819.critical_sword.util.unit

import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.util.able.Leveling
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

data class Radios(var x: Double, var y: Double, var z: Double): ConfigurationSerializable, Leveling<Radios> {

    companion object {
        val defaultRadios = Radios(0.0, 0.0, 0.0)
    }

    override fun serialize(): MutableMap<String, Double> = object: HashMap<String,Double>(
        mapOf(
            "xRadios" to x,
            "yRadios" to y,
            "zRadios" to z
        )
    ) {
        init {
            x = this["xRadios"]!!
            y = this["yRadios"]!!
            z = this["zRadios"]!!
        }
    }

    init {
        ConfigurationSerialization.registerClass(this::class.java)
    }

    override operator fun times(n: Double): Radios = Radios(x * n, y, z * n)

    override operator fun times(i: Int): Radios = times(i.toDouble())

    override fun times(levelStorage: AbilityStorage.Level<SwordAbility>): Radios = levelStorage.use(defaultRadios) { level -> times(level) }

    operator fun div(i: Int): Radios = Radios(x / i, y, z / i)
}