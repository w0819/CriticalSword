package com.github.w0819.critical_sword.meta_data

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util
import com.github.w0819.critical_sword.util.Util.divideToIntWithOutPoint
import com.github.w0819.critical_sword.util.Util.isHitWithSword
import com.github.w0819.critical_sword.util.Util.killEntity
import com.github.w0819.critical_sword.util.tracker.enma.StandMovingTracker
import com.github.w0819.critical_sword.util.tracker.trident.TridentKillingTracker
import com.github.w0819.critical_sword.util.unit.EntityAndNearestEntity
import com.github.w0819.critical_sword.util.unit.Radios
import io.github.monun.tap.config.Config
import io.github.monun.tap.config.Name
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack

@Name("sword-ability")
sealed class SwordAbility private constructor(val name: String ,val ingredients: List<ItemStack>){

    val key: NamespacedKey = NamespacedKey.minecraft(name)

    companion object {

        val abilities: Array<SwordAbility> = arrayOf(
            Enma,
            Trident
        )
    }

        abstract val maxLevel: Int

        fun afterHitEntityEffect(target: LivingEntity, player: Player, level: Int, plugin: SwordPlugin) {
            customAfterEffect(player, level, plugin)
            if (player.isHitWithSword(target)) customAfterEffectToEntity(target, player, level, plugin)
        }

        abstract fun customAfterEffect(player: Player, level: Int, plugin: SwordPlugin)

        protected abstract fun customAfterEffectToEntity(
            entity: LivingEntity,
            player: Player,
            level: Int,
            plugin: SwordPlugin
        )

        @Name("enma")
        object Enma : SwordAbility(
            name ="enma",
            ingredients =  listOf(
                ItemStack(Material.DRAGON_EGG)
            )
        ) {
            @Config("enma-max-level")
            override var maxLevel: Int = 5

            @Config("default-hit-ability-deep-length")
            var defaultDeepLength: Double = 400.0

            @Config("killing-area-radios")
            var radios = Radios(10.0, 10.0, 10.0)

            override fun customAfterEffect(player: Player, level: Int, plugin: SwordPlugin) =
                StandMovingTracker.create(player, defaultDeepLength * level)

            override fun customAfterEffectToEntity(entity: LivingEntity, player: Player, level: Int, plugin: SwordPlugin) {
                EntityAndNearestEntity.searchByType(
                    radios * level,
                    player,
                    entity.type
                ).forEach { nearest -> nearest.nearestEntity.killEntity() }
            }
        }
    @Name("Trident")
    object Trident: SwordAbility(
        name ="lighting",
        ingredients =  listOf(
            ItemStack(Material.TRIDENT)
        )
    ) {

        @Config("trident-max-level")
        override var maxLevel: Int = 10

        @Config("area-to-track-radios")
        var radios = Radios(40.0, 40.0, 40.0)

        @Config("sword-fake-entity-moving-delay")
        var movingDelay = 100L

        @Config("speed-of-sword-landing")
        var landingSpeed = 10.0 // this means the property of -y in the vector

        @Config("delay-of-spawn-of-sword")
        var swordSpawnDelay = 100L

        @Config("landing-length")
        var landingLength = 50.0


        override fun customAfterEffect(player: Player, level: Int, plugin: SwordPlugin) {
            val sword = player.inventory.itemInMainHand

            val swordEntity = TridentKillingTracker.setArmorStandPosition(
                player, sword, plugin
            )
            val targetEntities = EntityAndNearestEntity.defaultSearch(radios, player)

            player.inventory.remove(sword)

            TridentKillingTracker.strikeEntityByEntity(
                swordEntity,
                sword,
                player,
                targetEntities,
                movingDelay
            )
        }

        override fun customAfterEffectToEntity(entity: LivingEntity, player: Player, level: Int, plugin: SwordPlugin) {
            val sword = player.inventory.itemInMainHand
            val type = entity.type
            val aroundEntity = EntityAndNearestEntity.searchByType(radios, entity, type)


            TridentKillingTracker.strikeAllInOnce(
                player,
                sword,
                aroundEntity,
                swordSpawnDelay,
                landingLength,
                movingDelay,
                divideToIntWithOutPoint(landingLength, landingSpeed),
                plugin
            )
        }
    }



    fun loadConfig(plugin: SwordPlugin): Boolean = Util.compute(this, true, plugin)

    override fun toString(): String = this::class.simpleName!!

}
