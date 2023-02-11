package com.github.w0819.critical_sword.util.meta_data

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util.swords
import com.github.w0819.critical_sword.util.meta_data.SwordAbility.Companion.abilities
import com.github.w0819.critical_sword.util.storage.AbilityStorage
import com.github.w0819.critical_sword.util.storage.AbilityStorage.Companion.abilityLevel
import com.github.w0819.critical_sword.util.storage.AbilityStorage.Companion.hasAbility
import io.papermc.paper.event.player.PlayerArmSwingEvent
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class Sword private constructor(val vanilla: ItemStack,private val abilities: List<AbilityStorage>) {

    private val hasAbilities: List<AbilityStorage>
        get() = abilities.filter { storage -> hasAbility(storage.ability) }


    companion object {

        fun ItemStack.isSwordType(): Boolean = type in swords

        fun ItemStack.isSword(): Boolean = abilities.all { ability -> isSwordType() && hasAbility(ability) }
        operator fun invoke(sword: ItemStack): Sword {
            require(sword.isSwordType())
            return Sword(sword, AbilityStorage(sword))
        }

        operator fun invoke(sword: Sword, abilities: List<AbilityStorage>) = Sword(sword.vanilla, abilities)

        fun ItemStack.update(): ItemStack =
            apply {
                abilities.forEach { ability ->
                    val level = abilityLevel(ability)
                    itemMeta.persistentDataContainer.set(ability.key, PersistentDataType.INTEGER, level)
                }
            }
    }

    override operator fun equals(other: Any?): Boolean = when {
        other !is ItemStack -> false
        other == vanilla -> true
        other.type == vanilla.type && other.amount == vanilla.amount && other.itemMeta == vanilla.itemMeta -> true
        else -> false
    }

    override fun hashCode(): Int = vanilla.hashCode()

    init {
        vanilla.apply {
            addEnchantment(Enchantment.DAMAGE_ALL, 1)
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
            update()
        }
    }

    private fun abilityLevel(ability: SwordAbility): Int = abilities.find { it.ability == ability }?.level ?: 0

    private fun hasAbility(ability: SwordAbility): Boolean = abilityLevel(ability) != 0


    fun upgrade(ability: SwordAbility): Sword {
        val updatedAbilities = abilities.map { abilityStorage ->
            if (abilityStorage.ability == ability) abilityStorage.upgrade() else abilityStorage
        }
        return Sword(vanilla, updatedAbilities)
    }

    fun swingEffect(e: PlayerArmSwingEvent) {
        val player = e.player
        if (e.hand == EquipmentSlot.HAND && player.inventory.itemInMainHand.isSword()) {
            val loc = player.location
            val world = player.world

            world.spawnParticle(Particle.SWEEP_ATTACK, loc, 10, 0.0, 0.0, 5.0, 100.0) // sweep_attack ^ ^ ^ 10 0 0 5 100 normal @a
        }
    }

    fun activeAbilityAfterEffect(e: EntityDamageByEntityEvent, player: Player, plugin: SwordPlugin) {
        hasAbilities.forEach { abilityStorage ->  abilityStorage.run { ability.afterHitEntityEffect(player,level, plugin, e) } }
    }

}