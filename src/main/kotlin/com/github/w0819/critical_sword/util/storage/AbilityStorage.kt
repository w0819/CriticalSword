package com.github.w0819.critical_sword.util.storage

import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.meta_data.SwordAbility
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 *
 * */
data class AbilityStorage(val ability: SwordAbility, val level: Int) {
    companion object {
        operator fun invoke(sword: ItemStack): List<AbilityStorage> = SwordAbility.abilities.map { ability ->
            val level = sword.abilityLevel(ability)
            AbilityStorage(ability, level)
        }
        private fun ItemStack.abilityLevel(ability: SwordAbility): Int {
            require(isSwordType())
            return itemMeta.persistentDataContainer.get(ability.key, PersistentDataType.INTEGER) ?: 0
        }

        fun ItemStack.hasAbility(ability: SwordAbility): Boolean {
            require(isSwordType())
            return abilityLevel(ability) != 0
        }
    }

    fun upgrade(): AbilityStorage =
        if (ability.maxLevel != level) AbilityStorage(ability, level + 1) else this
}