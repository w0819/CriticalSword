package com.github.w0819.critical_sword.sword.meta_data

import com.github.w0819.critical_sword.plugin.CriticalSword
import com.github.w0819.critical_sword.util.Util
import io.github.monun.tap.config.Config
import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.EntityCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

sealed class SwordAbility private constructor(name: String, val ingredients: EnchantingItem): EnchantmentWrapper(name) {

    companion object {
        val LIGHTING = LIGHTING()
        val EXPLODING = EXPLODING()
        val BREAKING = BREAKING()
        val FIRING = FIRING()


        fun findAbilityContained(enchantingItems: EnchantingItem): SwordAbility = when(enchantingItems) {
            is EnchantingItem.Lighting -> LIGHTING
            is EnchantingItem.Exploding -> EXPLODING
            is EnchantingItem.Breaking -> BREAKING
            is EnchantingItem.Firing -> FIRING
        }

    }

    abstract fun <T: EntityDamageByEntityEvent> abilityEffect(event: T, sword: Sword): List<EntityDamageEvent>
    // the property of this function looks to need to be changed.

    sealed class EnchantingItem(vararg items: ItemStack): ArrayList<ItemStack>(items.toMutableList()) {
        class Lighting: EnchantingItem(ItemStack(Material.LIGHT))
        class Exploding: EnchantingItem(ItemStack(Material.CREEPER_HEAD))
        class Breaking: EnchantingItem(ItemStack(Material.NETHERITE_PICKAXE))
        class Firing: EnchantingItem(ItemStack(Material.FIRE_CHARGE))

        companion object {
            fun List<ItemStack>.isAllEnchantingItem(): Boolean = LIGHTING.ingredients.containsAll(this) || EXPLODING.ingredients.containsAll(this) || BREAKING.ingredients.containsAll(this) || FIRING.ingredients.containsAll(this)
            operator fun invoke(itemStacks: List<ItemStack>): EnchantingItem? =
                    when {
                        LIGHTING.ingredients.containsAll(itemStacks) -> Lighting()
                        EXPLODING.ingredients.containsAll(itemStacks) -> Exploding()
                        BREAKING.ingredients.containsAll(itemStacks) -> Breaking()
                        FIRING.ingredients.containsAll(itemStacks) -> Firing()
                        else -> null
                    }

        }
    }


    class LIGHTING : SwordAbility("LIGHTING", EnchantingItem.Lighting()) {
        private companion object {
            @Config
            var ConfigMaxLevel = 5
        }

        override fun translationKey(): String = toString()

        @Deprecated("Deprecated in Java", ReplaceWith("toString()"))
        override fun getName(): String = toString()

        override fun getMaxLevel(): Int = ConfigMaxLevel
        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = level * 1.9f

        override fun displayName(level: Int): Component = Component.text(
            """
                the light will strike the entities around the player!
                |when the level upgrades, more entities will be shot
            """.trimIndent()
        )

        override fun <T: EntityDamageByEntityEvent> abilityEffect(event: T, sword: Sword): List<EntityDamageEvent> {
            val player = event.entity as Player
            val loc = player.location
            val world = loc.world
            val level = sword.enchantments[this] ?: 0
            val entities = Util.getEntitiesInArea(level, player)

            return entities.map { entity ->
                val location = entity.location
                world.strikeLightningEffect(location)
                EntityDamageEvent(entity, EntityDamageEvent.DamageCause.LIGHTNING, 4.0 + getDamageIncrease(level, EntityCategory.NONE))
            }
        }

    }
    class EXPLODING: SwordAbility("EXPLORING", EnchantingItem.Exploding()) {
        private companion object {
            @Config
            var ConfigMaxLevel = 3
        }

        override fun translationKey(): String = toString()

        @Deprecated("Deprecated in Java", ReplaceWith("toString()"))
        override fun getName(): String = toString()

        override fun getMaxLevel(): Int = ConfigMaxLevel
        override fun displayName(level: Int): Component = Component.text(
            """the entities around the player will be exploded and died
                | when level upgrades, more entities will be exploded!
            """.trimIndent()
        )

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = level * 2.5f

        override fun <T: EntityDamageByEntityEvent> abilityEffect(event: T, sword: Sword): List<EntityDamageEvent> {
            val player = event.entity as Player
            val loc = player.location
            val world = loc.world
            val level = sword.enchantments[this] ?: 0
            val entities = Util.getEntitiesInArea(level, player)

            return entities.map { entity ->
                val powder = 4 + getDamageIncrease(level, EntityCategory.NONE)
                world.createExplosion(loc, powder, true, true)
                EntityDamageEvent(entity, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, getDamageIncrease(level, EntityCategory.NONE).toDouble())
            }
        }
    }
    class BREAKING: SwordAbility("BREAKING", EnchantingItem.Breaking()) {
        private companion object {
            @Config
            var ConfigMaxLevel = 10
        }

        override fun translationKey(): String = toString()

        @Deprecated("Deprecated in Java", ReplaceWith("toString()"))
        override fun getName(): String = toString()

        override fun getMaxLevel(): Int = ConfigMaxLevel
        override fun displayName(level: Int): Component = Component.text(
            """it will break everything which blocks you to walk forward
                |when level upgrade, it will break more Broadly!
                |in your $level, the entity in about ${Util.calculatorArea(level)}
            """.trimMargin()
        )

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = level * 1.1f
        override fun <T: EntityDamageByEntityEvent> abilityEffect(event: T, sword: Sword): List<EntityDamageEvent> {
            val player = event.entity as Player
            val defender = event.damager as LivingEntity
            val world = player.world
            val level = sword.enchantments[this] ?: 0

            Util.breakingBlocksWithArmorStand(world, player.location, defender.location, CriticalSword.instance /*fix this to change the property of super*/, 100.0 * level)
            return listOf()
        }
    }
    class FIRING: SwordAbility("FIRING", EnchantingItem.Firing()) {
        private companion object {
            @Config
            var ConfigMaxLevel = 3
        }

        override fun translationKey(): String = toString()

        @Deprecated("Deprecated in Java", ReplaceWith("toString()"))
        override fun getName(): String = toString()

        override fun getMaxLevel(): Int = ConfigMaxLevel
        override fun displayName(level: Int): Component = Component.text(
            """spread the fire into everywhere where who can fire stands
                |when level upgrades, more entities will fire!
            """.trimMargin()
        )

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = level * 1.1f
        override fun <T: EntityDamageByEntityEvent> abilityEffect(event: T, sword: Sword): List<EntityDamageEvent> {
            val player = event.entity as Player
            val world = player.world
            val loc = player.location
            val loc1 = loc.multiply(-1.0)
            val entities = world.getNearbyLivingEntities(loc1, loc1.distance(loc), 20.0)
            val level = sword.enchantments.filterKeys { it is FIRING }

            return entities.map { entity ->
                val location = entity.location.apply { y -= 1 }
                location.block.type = Material.FIRE

                EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FIRE, 4.0 + getDamageIncrease(level[this]!!, EntityCategory.NONE) )
            }
        }
    }

    final override fun getActiveSlots(): MutableSet<EquipmentSlot> = mutableSetOf()
    final override fun canEnchantItem(item: ItemStack): Boolean = item is Sword
    final override fun conflictsWith(other: Enchantment): Boolean = other is SwordAbility
    final override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.WEAPON
    final override fun getRarity(): EnchantmentRarity = EnchantmentRarity.UNCOMMON
    final override fun getStartLevel(): Int = 1
    final override fun isCursed(): Boolean = false
    final override fun isDiscoverable(): Boolean = false
    final override fun isTradeable(): Boolean = false
    final override fun isTreasure(): Boolean = false

    fun register() {
        try {
            Enchantment::class.java.getDeclaredField("acceptingNew").apply {
                isAccessible = true
                set(null, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        registerEnchantment(this)
    }

    override fun toString(): String = this::class.simpleName!!
}