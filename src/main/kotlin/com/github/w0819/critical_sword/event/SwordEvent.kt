package com.github.w0819.critical_sword.event

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util.isHitWithSword
import com.github.w0819.critical_sword.util.events.StandMovingCancelEvent
import com.github.w0819.critical_sword.util.events.SwordCreationEvent
import com.github.w0819.critical_sword.util.events.SwordCreationEvent.ArmorStandForCreation.Companion.isForSwordCreation
import com.github.w0819.critical_sword.util.events.TrackingCancelEvent
import com.github.w0819.critical_sword.util.meta_data.Sword
import com.github.w0819.critical_sword.util.meta_data.Sword.Companion.isSword
import com.github.w0819.critical_sword.util.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.util.tracker.SwordTracker.Companion.cancelToPickUp
import com.github.w0819.critical_sword.util.tracker.SwordTracker.Companion.startTracking
import io.papermc.paper.event.player.PlayerArmSwingEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.EquipmentSlot

class SwordEvent(private val plugin: SwordPlugin): Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerThrowItem(e: PlayerDropItemEvent) {
        val entity = e.itemDrop
        if (entity.itemStack.isSwordType()) {
            SwordPlugin.debugMessage("tracking Started")
            e.startTracking(plugin, entity)
        }
    }

    @EventHandler
    fun onPlayerSwingSword(e: PlayerArmSwingEvent) {
        val player = e.player
        val hand = e.hand
        val item = player.inventory.itemInMainHand

        if (hand == EquipmentSlot.HAND && item.isSword()) {
            val sword = Sword(item)
            sword.swingEffect(e)
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        when(val damager = e.damager) {
            is Player -> when {
                damager.isHitWithSword(e) -> {
                    val sword = Sword(damager.inventory.itemInMainHand)
                    sword.activeAbilityAfterEffect(e, damager, plugin)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onItemPickUp(e: PlayerAttemptPickupItemEvent) {
        val item = e.item
        if (item.itemStack.isSwordType()) {
            SwordPlugin.debugMessage("tracking ended")
            e.cancelToPickUp()
        }
    }

    @EventHandler
    fun onTrackingSwordCanceled(e: TrackingCancelEvent) {
        SwordPlugin.debugMessage("tracking ended")
        e.stopTracking()
    }

    @EventHandler
    fun onSwordCreation(e: SwordCreationEvent) {
        e.swordCreation()
    }

    @EventHandler
    fun onStandMovingCancel(e: StandMovingCancelEvent) {
        e.cancelMoving()
    }

    @EventHandler
    fun onPlayerGetSword(e: PlayerArmorStandManipulateEvent) {
        val item = e.armorStandItem
        val stand = e.rightClicked
        val player = e.player

        if (item.isSword()) if (stand.isForSwordCreation(player)) {
            e.isCancelled = true
            stand.remove()
        }
    }
}