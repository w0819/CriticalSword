package com.github.w0819.critical_sword.sword.meta_data

import com.github.w0819.critical_sword.util.Util
import io.github.monun.tap.config.Config
import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.EntityCategory
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
        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = when(level) {
            1 -> 7.0f
            2 -> 10.0f
            3 -> 13.0f
            4 -> 15.0f
            5 -> 20.0f
            else -> 0.0f
        }

        override fun displayName(level: Int): Component = Component.text(
            """
                
            """.trimIndent()
        )
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
            """
                
            """.trimIndent()
        )

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 1.0f
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

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f
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
                |when level upgrades, fire will spread more Broadly!
            """.trimMargin()
        )

        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f
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