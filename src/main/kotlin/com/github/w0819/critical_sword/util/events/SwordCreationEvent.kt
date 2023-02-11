package com.github.w0819.critical_sword.util.events

import com.github.w0819.critical_sword.util.meta_data.Sword
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.EulerAngle

/**
 *
 * */
class SwordCreationEvent(private val thrower: Player,private val item: Entity, private val sword: Sword): Event() {
    companion object {

        @JvmStatic
        private val handlers: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers

    }

    override fun getHandlers(): HandlerList = getHandlerList()

    fun swordCreation(): ArmorStandForCreation {
        val loc = item.location

        createEffect(loc)

        return ArmorStandForCreation(sword, loc.apply { y += 3 }, thrower)
    }

    private fun createEffect(loc: Location) {

        val world = loc.world
        val firesX = listOf(
            0.0, 1.0, 3.0
        )
        val firesZ = listOf(
            0.0, 2.0, 3.0
        )

        firesX.flatMap { x ->
            firesZ.map {  z ->
                Location(world, x, 0.0, z)
            }
        }.map { loc1 ->
            loc.add(loc1)
        }.forEach { fire ->
            fire.block.type = Material.FIRE
        }


        world.strikeLightningEffect(loc)
    }

    class ArmorStandForCreation private constructor(@Suppress("unused") val armorStand: ArmorStand) {
        companion object {
            @JvmStatic
            private val armorTag: NamespacedKey = NamespacedKey.minecraft("armor_for_sword_creation")

            @JvmStatic
            fun ArmorStand.isForSwordCreation(player: Player): Boolean = persistentDataContainer.has(armorTag) && customName() == player.name()
            operator fun invoke(sword: Sword,loc: Location, thrower: Player): ArmorStandForCreation {
                val world = loc.world
                val armorStand = world.spawn(loc.apply { z += 3 }, ArmorStand::class.java).apply {
                    setProperty()
                    addCustomizedTag(thrower)
                    setItem(EquipmentSlot.HAND, sword.vanilla)
                }

                return ArmorStandForCreation(armorStand)
            }

            @JvmStatic
            private fun ArmorStand.setProperty(): ArmorStand = apply {
                isVisible = false
                setGravity(false)
                rightArmPose = EulerAngle(265.0,100.0,177.0)
                setArms(true)
                if (!hasArms()) setArms(true)
            }

            @JvmStatic
            private fun ArmorStand.addCustomizedTag(thrower: Player): ArmorStand = apply {
                persistentDataContainer.set(armorTag, PersistentDataType.STRING, "")
                customName(Component.text(thrower.name))
            }
        }
    }
}