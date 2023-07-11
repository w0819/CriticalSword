package com.github.w0819.critical_sword.meta_data

import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.util.ListUtil.swordTypes
import com.github.w0819.critical_sword.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class Sword(val vanilla: ItemStack,val storage: AbilityStorage) {
    companion object {
        fun ItemStack.isSwordType(): Boolean = type in swordTypes

        fun ItemStack.isSword(): Boolean = let { candidate -> candidate.isSwordType() && Sword(candidate.clone()).storage.havingAbilities.isNotEmpty() }

        fun ItemStack.isSwordFor(ability: SwordAbility): Boolean = let { candidate -> candidate.isSword() && Sword(candidate).isSwordFor(ability) }

        operator fun invoke(sword: ItemStack): Sword {
            require(sword.isSwordType()) { "Illegal state of sword's type: ${sword.type}" }
            val storage = AbilityStorage(sword)

            return Sword(sword, storage)
        }
    }

    val damage: Double = when(vanilla.type) {
        Material.WOODEN_SWORD -> 4.0
        Material.GOLDEN_SWORD -> 4.0
        Material.STONE_SWORD -> 5.0
        Material.IRON_SWORD -> 6.0
        Material.DIAMOND_SWORD -> 7.0
        Material.NETHERITE_SWORD -> 8.0
        else -> throw IllegalStateException("Illegal state of sword's type: ${vanilla.type}")
    }

    fun upgrade(ability: SwordAbility): Sword {
        val level = storage[ability]?.level ?: 0

        return apply { if (level != ability.maxLevel) storage.upgrade(ability) }
    }

    fun isSwordFor(ability: SwordAbility): Boolean = ability in storage.havingAbilities.keys

    private fun initLore(havingAbilities: Map<SwordAbility,Int>): MutableList<Component> {

        val lists =
            listOf("this sword has:") + if (havingAbilities.isNotEmpty()) havingAbilities.map { (ability, level) ->
                "[$ability]: $level"
            } else listOf("Nothing!")

        val imMutable =
            if (havingAbilities.isEmpty()) lists.map(Util::failMessage) else lists.map(Util::successMessage)

        return imMutable.toMutableList()
    }

    fun updateToLore(lore: List<Component>) = vanilla.lore(
        vanilla.lore()?.plus(lore) ?: throw IllegalStateException("the Illegal State of un-Init sword's lore")
    )

    init {
        val abilities = storage.havingAbilities

        vanilla.itemMeta.isUnbreakable = true

        vanilla.lore(
            initLore(abilities)
        )

        vanilla.addEnchantment(Enchantment.DAMAGE_ALL, 1)
    }
}