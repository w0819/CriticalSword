package com.github.w0819.critical_sword.meta_data

import com.github.w0819.critical_sword.meta_data.SwordAbility.Companion.abilities
import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util.swords
import com.github.w0819.critical_sword.util.storage.AbilityStorage
import com.github.w0819.critical_sword.util.storage.AbilityStorage.Companion.hasAbility
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
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

        fun Sword.updateToVanilla(): Sword =
            apply {
                abilities.forEach { abilityStorage ->
                    val (ability, level) = abilityStorage
                    vanilla.itemMeta.persistentDataContainer.set(ability.key, PersistentDataType.INTEGER, level)
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
            addEnchantment(Enchantment.DAMAGE_ALL, 1) // sharpness 1
            itemMeta = itemMeta.apply {
                isUnbreakable = true // unbreakable
            }
        }
    }

    private fun abilityLevel(ability: SwordAbility): Int = abilities.find { it.ability == ability }?.level ?: 0

    private fun hasAbility(ability: SwordAbility): Boolean = abilityLevel(ability) != 0


    fun upgrade(ability: SwordAbility): Sword {
        val updatedAbilities = abilities.map { abilityStorage ->
            if (abilityStorage.ability == ability) abilityStorage.upgrade() else abilityStorage
        }

        return Sword(vanilla, updatedAbilities).apply { updateToVanilla() }
    }

    fun swingEffect(player: Player ,plugin: SwordPlugin) {
        val loc = player.location
        val world = player.world

        world.spawnParticle(Particle.SWEEP_ATTACK, loc, 10, 0.0, 0.0, 5.0, 100.0)
        if (hasAbilities.isNotEmpty()) hasAbilities.forEach { storage -> storage.ability.customAfterEffect(player, storage.level, plugin) }
    }

    fun activeAbilityAfterEffect(target: LivingEntity, player: Player, plugin: SwordPlugin): List<AbilityStorage> =
        hasAbilities.onEach { abilityStorage ->  abilityStorage.run { ability.afterHitEntityEffect(target ,player,level, plugin) } }

}