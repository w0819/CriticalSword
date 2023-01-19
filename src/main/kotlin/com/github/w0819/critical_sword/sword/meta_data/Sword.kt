package com.github.w0819.critical_sword.sword.meta_data

import com.github.w0819.critical_sword.plugin.CriticalSword
import com.github.w0819.critical_sword.util.Util
import io.github.monun.tap.fake.FakeEntity
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Item
import org.bukkit.entity.LightningStrike
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

sealed class Sword (vanillaSword: VanillaSwordType) : ItemStack(vanillaSword) {

    lateinit var spawnParticle: BukkitTask
        private set

    sealed class VanillaSwordType(sword: ItemStack): ItemStack(sword) {
        class WoodenSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        class StoneSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        class IronSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        class GoldenSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        class DiamondSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        class NetheriteSword(vanSword: ItemStack): VanillaSwordType(vanSword)
        companion object {
            /**
             * judge whether it's sword nor not to something ItemStack
             * */
            fun ItemStack.isSword(): Boolean = type in Util.Vanilla
            operator fun invoke(itemStack: ItemStack): VanillaSwordType? = if (itemStack.isSword()) when(itemStack.type) {
                Material.WOODEN_SWORD -> WoodenSword(itemStack)
                Material.STONE_SWORD -> StoneSword(itemStack)
                Material.IRON_SWORD -> IronSword(itemStack)
                Material.GOLDEN_SWORD -> GoldenSword(itemStack)
                Material.DIAMOND_SWORD -> DiamondSword(itemStack)
                Material.NETHERITE_SWORD -> NetheriteSword(itemStack)
                else -> throw RuntimeException("Here is unreachable rea")
            } else null
        }

        override fun toString(): String = this::class.simpleName!!
    }
    companion object {
        /**
         * the function which transports Vanilla Sword type to be specified
         */
        operator fun invoke(sword: VanillaSwordType): Sword = when(sword) {
            is VanillaSwordType.WoodenSword -> WoodenSword(sword)
            is VanillaSwordType.StoneSword -> Hassaikai(sword)
            is VanillaSwordType.IronSword -> Enma(sword)
            is VanillaSwordType.GoldenSword -> GoldenSword(sword)
            is VanillaSwordType.DiamondSword -> Kashu(sword)
            is VanillaSwordType.NetheriteSword -> NetheriteSword(sword)

        }

        operator fun invoke(sword: VanillaSwordType, abilityIngredient: SwordAbility.EnchantingItem): Sword {
            val swordType = invoke(sword)
            val ability = SwordAbility.findAbilityContained(abilityIngredient)

            return invoke(swordType,ability)
        }

        operator fun invoke(sword: Sword, abilityIngredient: SwordAbility): Sword {
            val swordEnchantments = sword.enchantments
            sword.addEnchantment(abilityIngredient, swordEnchantments[abilityIngredient] ?: 1)

            return sword
        }

        fun swordCreation(swordItem: Item,sword: VanillaSwordType, enchantingItem: SwordAbility.EnchantingItem, plugin: CriticalSword): Item {
            val loc = swordItem.location

            val swordCreated = Sword(sword,enchantingItem)
            return swordCreated.creationEffect(loc, plugin).bukkitEntity
        }
    }

    private fun creationEffect(loc: Location, plugin: CriticalSword): FakeEntity<Item> {
        val world = loc.world

        val fireLocs = listOf(
            Location(world,1.0,0.0,1.0),
            Location(world,0.0,0.0,1.0),
            Location(world, -1.0, 0.0, 1.0),
            Location(world, 0.0, 0.0, 0.0),
            Location(world,  0.0, 0.0, -1.0)
        )

        fireLocs.forEach {
            val fireLoc = loc.add(it)
            fireLoc.block.setType(Material.FIRE, false)
        }
        world.spawn(loc, LightningStrike::class.java)

        spawnParticle = plugin.bukkitScheduler.runTaskTimer(plugin, Runnable { world.spawnParticle(Particle.SOUL, loc, 3000)}, 0L, 1L)

        return plugin.fakeServer.spawnItem(loc, this).apply {
            location.apply {
                yaw = 0.0f
                pitch = - 90.0f
                y -= 0.5
            }
        }
    }

    /**
     * the crazy sword which type is iron sword
     * **/
    class Enma(sword: VanillaSwordType.IronSword) : Sword(sword)
    /**
     * the crazy sword which type is stone sword
     * */
    class Hassaikai(sword: VanillaSwordType.StoneSword) : Sword(sword)
    /**
     * the crazy sword which type is wooden sword
     * */
    class WoodenSword(sword: VanillaSwordType.WoodenSword) : Sword(sword) // the name of this class is not specified yet
    /**
     * the crazy sword which typ is golden sword
     * */
    class GoldenSword(sword: VanillaSwordType.GoldenSword): Sword(sword)   // the name of this class is not specified yet
    /**
     * the crazy sword which type is Diamond sword
     * */
    class Kashu(sword: VanillaSwordType.DiamondSword) : Sword(sword)    // the name of this class is not specified yet
    /**
     * the crazy sword which type is Netherite Sword
     * */
    class NetheriteSword(sword: VanillaSwordType.NetheriteSword) : Sword(sword)

    override fun toString(): String = this::class.simpleName!!

    init {
        itemMeta = itemMeta.apply {
            isUnbreakable = true // the sword will not be broken
        }
    }
}