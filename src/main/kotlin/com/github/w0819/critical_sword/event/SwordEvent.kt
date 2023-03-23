package com.github.w0819.critical_sword.event

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSword
import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util
import com.github.w0819.critical_sword.util.Util.vectorTo
import com.github.w0819.critical_sword.util.sword_creation.SwordCreation
import com.github.w0819.critical_sword.util.sword_creation.SwordCreation.Companion.isForSwordCreation
import com.github.w0819.critical_sword.util.tracker.MovingTracker
import io.github.monun.invfx.openFrame
import io.github.monun.tap.config.Config
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class SwordEvent(private val plugin: SwordPlugin): Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        plugin.fakeServer.addPlayer(player)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player

        plugin.fakeServer.removePlayer(player)
    }

    @Config
    var targetingSwordMaxDistance = 100 // blocks

    @Config
    var targetingSwordMovingDelay = 50L // 0.2 seconds

    @Config
    var targetingSwordMovingTime = 3 // total 0.6 seconds delays

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val item = e.item ?: ItemStack(Material.AIR)
        val loc = player.location
        if (item.isSwordType()) {
            val sword = Sword(item)
            when (e.action) {
                PHYSICAL -> player.openFrame(SwordCreation.optionInventory(player)(sword)) // 흴


                LEFT_CLICK_BLOCK, LEFT_CLICK_AIR -> { // 왼쪽

                    val target = Util.nearestLivingEntityInArea(
                        Util.areaFromPlayerToPlayerLooking(targetingSwordMaxDistance, player, targetingSwordMaxDistance),
                        targetingSwordMaxDistance.toDouble(),
                        player.location
                    )

                    val speed = when(target) {
                        null -> {
                            val direction = player.location.direction
                            direction.multiply(targetingSwordMaxDistance)
                        }
                        else -> loc.vectorTo(target.location)
                    }.multiply(1/ targetingSwordMovingTime)

                    val swordEntity = plugin.fakeServer.spawnItem(loc, sword.vanilla)

                    MovingTracker.fakeEntityDefaultMoving(
                        listOf(swordEntity),
                        targetingSwordMovingDelay,
                        targetingSwordMovingTime,
                        speed
                    ).completionAfter { fakeSwordEntity ->
                        target?.let { sword.activeAbilityAfterEffect(it,  player, plugin) }
                        fakeSwordEntity.remove()
                    }
                }

                RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR -> sword.swingEffect(player, plugin) // 오른쪽
            }
        }
    }

    @EventHandler
    fun onPlayerPickUpOmArmorStand(e: PlayerArmorStandManipulateEvent) {
        val armorStand = e.rightClicked
        val player = e.player
        val armorItem = e.armorStandItem
        val slot = e.slot
        if (armorItem.isSword() && slot == EquipmentSlot.HAND) if (!armorStand.isForSwordCreation(player)) {
            e.isCancelled = true
            SwordPlugin.debugMessage("player picked up from armor stand event was canceled")
        } else {
            SwordPlugin.debugMessage("${player.name} picked up $armorItem from $armorStand")
        }
    }

    @EventHandler
    fun onEntityDamageByPlayer(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val entity = e.entity as? LivingEntity ?: return

        when (e.cause) {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> {
                val item = player.inventory.itemInMainHand
                if (item.isSword()) {
                    val sword = Sword(item)
                    sword.activeAbilityAfterEffect(entity, player, plugin)
                }
            }
            else -> {

            }
        }
    }
}