package com.github.w0819.critical_sword.util.tracker

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.events.SwordCreationEvent
import com.github.w0819.critical_sword.util.events.TrackingCancelEvent
import com.github.w0819.critical_sword.util.meta_data.Sword
import com.github.w0819.critical_sword.util.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.util.meta_data.SwordAbility
import org.bukkit.entity.Item
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

class SwordTracker private constructor(val target: Sword) {
    companion object {

        private val trackingSwords: HashMap<SwordTracker, BukkitTask> = hashMapOf()

        fun PlayerDropItemEvent.startTracking(plugin: SwordPlugin, sword: Item): SwordTracker?  =
            if (sword.itemStack.isSwordType()) {
                val targetSword = Sword(sword.itemStack)
                val world = sword.world
                val loc = sword.location
                val run = Runnable {
                    val itemsAround = world.getNearbyEntitiesByType(Item::class.java, loc, 1.0, 1.0, 1.0).map { it.itemStack }

                    if (SwordAbility.isAbilityIngredients(itemsAround)) {
                        SwordPlugin.debugMessage("find ingredients!")
                        plugin.server.pluginManager.apply {
                            callEvent(TrackingCancelEvent(targetSword))


                            callEvent(SwordCreationEvent(player,sword, combineSwordAndIngredients(targetSword, itemsAround)))

                        }
                        SwordPlugin.debugMessage("event calling completed!")
                    }
                }
                SwordPlugin.debugMessage("start timer to track")
                val task = plugin.server.scheduler.runTaskTimer(plugin, run, 1L, 1L)
                SwordTracker(targetSword).uploadOn(task)
            } else null

        fun PlayerAttemptPickupItemEvent.cancelToPickUp(): Boolean {
            require(item.itemStack.isSwordType())
            val sword = Sword(item.itemStack)
            SwordPlugin.debugMessage("tracking canceled by picked up the target")
            return cancelTasking(sword)
        }

         fun cancelTasking(sword: Sword): Boolean {
            if (trackingSwords.keys.any { tracker ->
                    val targetSword = tracker.target
                    targetSword == sword
                }) {
                return trackingSwords.keys.find { sword == it.target }?.stopTracking() ?: return false
            }
            return false
        }

        private fun combineSwordAndIngredients(sword: Sword, ingredients: List<ItemStack>): Sword {
            require(SwordAbility.isAbilityIngredients(ingredients))
            val ability = SwordAbility.toAbilityIngredients(ingredients)
            return sword.upgrade(ability)
        }

    }

    fun uploadOn(bukkitTask: BukkitTask): SwordTracker {
        trackingSwords[this] = bukkitTask
        return this
    }

    fun stopTracking(): Boolean =
        let { tracker ->
            val task = trackingSwords[tracker]?.cancel()
            trackingSwords.remove(tracker).run { task != null }
        }
}