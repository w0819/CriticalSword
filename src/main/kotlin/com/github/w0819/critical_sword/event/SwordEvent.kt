package com.github.w0819.critical_sword.event

import com.github.w0819.critical_sword.plugin.CriticalSword
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility.EnchantingItem
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility.EnchantingItem.Companion.isAllEnchantingItem
import com.github.w0819.critical_sword.sword.meta_data.Sword
import com.github.w0819.critical_sword.sword.meta_data.Sword.VanillaSwordType.Companion.isSword
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.player.PlayerDropItemEvent

class SwordEvent(private val plugin: CriticalSword) : Listener {
    @EventHandler
    fun onPlayerThrowItem(e: PlayerDropItemEvent) {
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

                    Sword.swordCreation(dropItem, swordType, enchantingItem)
                }


            }
        }

        scheduler.runTaskTimer(plugin, f(dropItem), 1, 50)

    }



    @EventHandler
    fun onSwordCombust(e: EntityCombustEvent) {
        val item = e.entity as? Item ?: return
        if (item.itemStack is Sword)
            e.isCancelled = true
    }

}