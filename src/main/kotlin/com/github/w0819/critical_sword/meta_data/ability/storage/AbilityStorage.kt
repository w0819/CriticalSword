package com.github.w0819.critical_sword.meta_data.ability.storage

import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class AbilityStorage(
    sword: ItemStack
): SwordDataStorage<SwordAbility, AbilityStorage.Level<SwordAbility>>(
    sword, NamespacedKey.minecraft("ability_storage"),
    Level::class, SwordAbility.abilities,
    1
) {
    private fun getOrDefault(ability: SwordAbility): Level<SwordAbility> = getOrDefault(ability, Level(ability, 1))

    fun <T: SwordAbility> get(ability: T): Level<T> = Level(ability, get(ability)?.level ?: 0)

    val havingAbilities: Map<SwordAbility, Int> = SwordAbility.abilities.associateWith { ability ->
        getOrDefault(ability).level
    }

    class Level<out T: SwordAbility> (
        val ability: SwordAbility,val level: Int
    ) {
        val nextLevel: () -> Level<T> = { Level(ability, level + 1) }

        fun use(task: (Int) -> Unit) { if (level != 0) task(level) }

        fun <U> use(default: U, task: (Int) -> U): U = if (level != 0) task(level) else default

        operator fun times(i: Int): Int = level * i

        operator fun times(d: Double): Double = level * d

        override fun toString(): String = ability.toString()
    }

    fun <T: SwordAbility> upgrade(ability: T): AbilityStorage {
        val nextLevel = getOrDefault(ability).nextLevel

        return apply { set(ability, nextLevel()) }
    }
}