package com.github.w0819.critical_sword.event

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSword
import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util
import com.github.w0819.critical_sword.util.manager.event.ArmorItemGetter
import com.github.w0819.critical_sword.util.manager.event.ArmorItemGetter.canPickUpArmorItem
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler
import com.github.w0819.critical_sword.util.manager.finder.ItemSearcher
import com.github.w0819.critical_sword.util.unit.XYZ
import io.github.monun.tap.config.Config
import io.papermc.paper.event.entity.EntityDamageItemEvent
import org.bukkit.Particle
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.ThrowableProjectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.*

class SwordEvent: Listener {
    @Config("ability-ingredient-searching-delay")
    private var searchDelay = 7L

    @Config("ability-ingredients-search-max-try")
    private var searchMaxTry = 700

    @Config("ability-sword-casting-times")
    private var castingTimes = 30

    @Config("ability-sword-casting-delay")
    private var castingDelay = 100L

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        SwordPlugin.fakeServer.addPlayer(player)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        SwordPlugin.fakeServer.removePlayer(player)
    }

    private val itemSearcher = ItemSearcher(
        searchMaxTry, searchDelay, XYZ.defaultXYZ,true, castingTimes, castingDelay
    ) { world ->
        { loc ->
            { times ->
                val (xOffSet, yOffSet, zOffSet) = XYZ.defaultXYZ.timesWIthOutY(times)
                world.spawnParticle(Particle.ENCHANTMENT_TABLE,loc, 500 * times, xOffSet, yOffSet, zOffSet, 2.0 * times)
            }
        }
    }
    @EventHandler
    fun onPlayerThrowItem(e: PlayerDropItemEvent) {
        val droppedItem = e.itemDrop

        if (droppedItem.itemStack.isSwordType()) {
            val isIngredient: (List<Item>) -> Boolean = { items -> SwordAbility.isIngredient(items.map { it.itemStack }) }
            val toAbility: (List<Item>) -> List<SwordAbility> = { items -> SwordAbility.toAbility(items.map { it.itemStack }) }

            itemSearcher.itemSearch(droppedItem, isIngredient, toAbility) { target ->
                { abilities ->
                    val world = target.world
                    val loc = target.location

                    val item = target.itemStack
                    val sword = Sword(item)

                    world.strikeLightningEffect(loc)
                    world.spawnParticle(Particle.ITEM_CRACK, loc, 500, 15.0, 15.0, 15.0 ,2.0, sword.vanilla)


                    abilities.forEach { ability ->
                        sword.upgrade(ability)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onSwordUsed(e: EntityDamageByEntityEvent) {
        val user = e.entity

        if (user is LivingEntity) {
            val item = user.activeItem
            if (item.isSword()) { SwordUseByUserHandler.call(e, user, Sword(item)) }
        }
    }

    @EventHandler
    fun onSwordUsed(e: EntityDamageItemEvent) {
        val entity = e.entity as? LivingEntity ?: return
        val item = e.item

        if (item.isSword()) {
            val sword = Sword(item)

            SwordUseByUserHandler.call(e, entity, sword)
        }
    }

    @EventHandler
    fun onSwordUsed(e: PlayerInteractEvent) {
        val user = e.player
        val item = e.item ?: return

        if (item.isSword()) SwordUseByUserHandler.call(e, user, Sword(item))
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        val projectile = e.entity

        if (projectile is ThrowableProjectile && projectile.item.isSwordType()) {
            val sword = Sword(projectile.item)
            val user = projectile.shooter as? Player ?: return

            SwordUseByUserHandler.call(e, user, sword)
        }
    }

    @EventHandler
    fun onArmorStandPickUpManage(e: PlayerArmorStandManipulateEvent) {
        val armorStand = e.rightClicked
        val player = e.player

        if (ArmorItemGetter.isManaged(armorStand)) {
            if (!player.canPickUpArmorItem(armorStand))
                player.apply {
                    sendMessage(
                        Util.failMessage("this armor has been managed for only getting this' owner")
                    )
                }.run { e.isCancelled = true }
            else armorStand.remove()
        }
    }
}