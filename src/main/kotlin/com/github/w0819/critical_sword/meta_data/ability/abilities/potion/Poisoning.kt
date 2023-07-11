package com.github.w0819.critical_sword.meta_data.ability.abilities.potion

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.meta_data.ability.storage.PotionEffectsStorage
import com.github.w0819.critical_sword.util.ListUtil
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler
import com.github.w0819.critical_sword.util.manager.finder.ItemSearcher
import com.github.w0819.critical_sword.util.unit.XYZ
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Suppress("unused")
object Poisoning: SwordAbility(
    PoisoningListener,
    { ingredient ->
        ingredient.type == Material.GLASS_BOTTLE
    }
) {
    override val eventManager: () -> SwordUseByUserHandler.AbilityEventManager<out SwordAbility> =
        { PoisoningEventManager }

    override var maxLevel: Int = 3

    object PoisoningListener : AbilityListener() {

        @EventHandler
        fun onPlayerHoldSword(e: PlayerItemHeldEvent) {
            val player = e.player
            val inventory = player.inventory

            val item = inventory.getItem(EquipmentSlot.HAND)

            if (item.isSwordType()) {
                val potionEffectStorage = PotionEffectsStorage(item)

                val potionEffects = potionEffectStorage.defaultPotionEffects

                player.addPotionEffects(potionEffects.map { it.effect })

                potionEffects.forEach { storage -> potionEffectStorage.remove(storage.type) }
            }
        }

        val entityDamageByPlayer: (EntityDamageByEntityEvent) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<Poisoning>) -> Unit = { event ->
            { entity ->
                { sword ->
                    { _ ->
                        val player = event.damager
                        if (player is Player) {
                            val potionEffectsStorage = PotionEffectsStorage(sword.vanilla)

                            val splashPotionEffects = potionEffectsStorage.splashPotionEffects
                            val lingeringPotionEffects = potionEffectsStorage.lingeringPotionEffects

                            entity.addPotionEffects(splashPotionEffects.map { it.effect })

                            val world = entity.world
                            val loc = entity.location

                            val sameTypeEntities = world.getNearbyLivingEntities(loc, 20.0, 20.0, 20.0) { acc ->
                                acc.type == entity.type && (acc !is Player || !acc.scoreboard.teams.any { team -> team in player.scoreboard.teams || acc.uniqueId != player.uniqueId || acc.uniqueId != entity.uniqueId })
                            }

                            sameTypeEntities.forEach { acc -> acc.addPotionEffects(lingeringPotionEffects.map { it.effect }) }

                            (splashPotionEffects + lingeringPotionEffects).forEach { potionStorage ->
                                potionEffectsStorage.remove(potionStorage.type)
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        fun onPlayerThrowSword(e: PlayerDropItemEvent) {
            val item = e.itemDrop

            if (item.itemStack.isSwordType()) {
                val sword = Sword(item.itemStack.clone())

                val potionStorage = PotionEffectsStorage(item.itemStack)

                val isPotion: (List<Item>) -> Boolean = { items ->
                    (sword.storage[Poisoning]?.level != 0) && items.any { item -> item.itemStack.type in ListUtil.potionMaterials }
                }

                val toPotionType: (List<Item>) -> List<ItemStack> = { items ->
                    sword.storage[Poisoning]?.use(null) { level ->
                        items.map { it.itemStack }.filter { it.type in ListUtil.potionMaterials }.take(level)
                    } ?: listOf()
                }

                val itemSearcher = ItemSearcher(
                    10, 50L, XYZ.defaultXYZ
                )

                itemSearcher.itemSearch(
                    item, isPotion, toPotionType
                ) { target ->
                    { potions ->
                        val world = target.world
                        val loc = target.location

                        world.strikeLightningEffect(loc)

                        potions.forEach { potion -> potionStorage.setPotionEffect(potion) }
                    }
                }
            }
        }
    }

    object PoisoningEventManager : SwordUseByUserHandler.AbilityEventManager<Poisoning>(
        Poisoning, SwordUseByUserHandler.AbilityTask(PoisoningListener.entityDamageByPlayer, EntityDamageByEntityEvent::class)
    )
}

