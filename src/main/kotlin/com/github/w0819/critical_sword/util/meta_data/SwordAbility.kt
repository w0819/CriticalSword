package com.github.w0819.critical_sword.util.meta_data

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util.isHitWithSword
import com.github.w0819.critical_sword.util.Util.killSameTypeEntityAround
import com.github.w0819.critical_sword.util.tracker.StandMover
import io.github.monun.tap.config.Config
import io.github.monun.tap.config.Name
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Name("sword-ability")
sealed class SwordAbility private constructor(name: String,@Config("ingredients") val ingredients: List<ItemStack>){

    @Config(required = false)
    val key: NamespacedKey = NamespacedKey.minecraft(name)

    companion object {

        val abilities: Array<SwordAbility> = arrayOf(
            Enma
        )

        fun isAbilityIngredients(items: List<ItemStack>): Boolean  {
            return  abilities.any { items.containsAll(it.ingredients) }
        }

        fun toAbilityIngredients(items: List<ItemStack>): SwordAbility {
            require(isAbilityIngredients(items)) { "fail to cast as Sword Ability because of items is not ingredients for ability" }
            return abilities.find { ability -> items.containsAll(ability.ingredients) }!!
        }

    }

    abstract val maxLevel: Int

    fun afterHitEntityEffect(player: Player, level: Int, plugin: SwordPlugin, e: EntityDamageByEntityEvent) {
        customAfterEffect(player, level, plugin)
        if (player.isHitWithSword(e)) customAfterEffectToEntity(e.entity, player, level, plugin)
    }

    protected abstract fun customAfterEffect(player: Player, level: Int, plugin: SwordPlugin)

    protected abstract fun customAfterEffectToEntity(entity: Entity, player: Player, level: Int, plugin: SwordPlugin)

    object Enma: SwordAbility("enma", listOf(ItemStack(Material.DRAGON_EGG))) {
        @Config("enma-max-level")
        override var maxLevel: Int = 5

        @Config("default-deep-length")
        var defaultDeepLength: Double = 400.0

        @Config("killing-area-x-length")
        var xRadi = 10.0

        @Config("killing-area-y-length")
        var yRadi = 10.0

        @Config("killing-area-z-length")
        var zRadi = 10.0

        override fun customAfterEffect(player: Player, level: Int, plugin: SwordPlugin) =
            StandMover.create(player, defaultDeepLength * level, plugin)

        override fun customAfterEffectToEntity(entity: Entity, player: Player, level: Int, plugin: SwordPlugin) {
            if (entity is LivingEntity) killSameTypeEntityAround(entity, xRadi, yRadi, zRadi)
        }
    }
}
