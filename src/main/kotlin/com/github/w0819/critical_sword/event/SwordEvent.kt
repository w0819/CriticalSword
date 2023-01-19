package com.github.w0819.critical_sword.event

import com.github.w0819.critical_sword.plugin.CriticalSword
import com.github.w0819.critical_sword.sword.meta_data.Sword
import com.github.w0819.critical_sword.sword.meta_data.Sword.VanillaSwordType.Companion.isSword
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility.EnchantingItem
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility.EnchantingItem.Companion.isAllEnchantingItem
import com.github.w0819.critical_sword.util.Util
import com.github.w0819.critical_sword.util.Util.tail
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent

class SwordEvent(private val plugin: CriticalSword) : Listener {
    @EventHandler
    fun makeArmorStandFollowPlayerEye(e: PlayerMoveEvent) {
        val player = e.player
        val loc = e.from
        val world = player.world
        val wherePlayerIsLooking = player.getTargetBlock(null, 100).location.add(Location(world, 0.0, -0.5, 2.0))
        val armorStand = player.world.spawnEntity(loc, EntityType.ARMOR_STAND).apply {
            val entity = this as ArmorStand // unSafety casting happen here
            entity.apply {
                isVisible = true
                setGravity(false)
            }
        }

        armorStand.teleport(loc.add(wherePlayerIsLooking))

    }
    @EventHandler
    fun onHitArmorStand(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val sword = player.inventory.itemInMainHand as? Sword ?: return
        val enchantments = sword.enchantments.keys.toList()
        fun activeAbility(enchantments: List<Enchantment>) {
            if (enchantments.isNotEmpty()) {

                val enchantment = enchantments.first()

                if (enchantment is SwordAbility)
                    enchantment.abilityEffect(e, sword).forEach(plugin.server.pluginManager::callEvent)
                else activeAbility(enchantments.tail())
            }
        }


        when(val defender = e.entity as? LivingEntity ?: return) {
            is ArmorStand -> activeAbility(enchantments)
            else -> {
                Util.killEntitiesAroundTheEntity(defender, 100.0, 100.0)
                activeAbility(enchantments)
            }
        }
    }
    @EventHandler
    fun onPlayerThrowItem(e: PlayerDropItemEvent) {
        val world = e.player.world
        val scheduler = plugin.bukkitScheduler
        val dropItem = if (e.itemDrop.itemStack.isSword()) e.itemDrop else return

        val f: (Item) -> () -> Unit = { item -> tick@
            {
                val itemStack = item.itemStack
                val itemInBlock = item.location.getNearbyEntitiesByType(
                    Item::class.java, 1.0,1.0,1.0
                ).toList().map { it.itemStack }

                if (itemInBlock.isAllEnchantingItem()) {
                    val enchantingItem = EnchantingItem(itemInBlock)!!
                    val swordType = Sword.VanillaSwordType(itemStack)!!

                    val swordItem = Sword.swordCreation(dropItem, swordType, enchantingItem, plugin)
                    plugin.bukkitScheduler.runTaskTimer(plugin, Runnable { world.spawnParticle(Particle.SOUL, swordItem.location, 3000) }, 1L,0L)
                }


            }
        }
        scheduler.runTaskTimer(plugin, f(dropItem), 1, 50)
    }

    @EventHandler
    fun onClickSword(e: PlayerInteractEntityEvent) {
        val entity = e.rightClicked as? Item ?: return
        val item = entity.itemStack as? Sword ?: return
        val player = e.player

        player.inventory.addItem(item)
        entity.let {
            it.remove()
            plugin.bukkitScheduler.cancelTask(item.spawnParticle.taskId)
        }
    }




    @EventHandler
    fun onSwordCombust(e: EntityCombustEvent) {
        val item = e.entity as? Item ?: return
        if (item.itemStack is Sword)
            e.isCancelled = true
    }

}