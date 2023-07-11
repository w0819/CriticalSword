package com.github.w0819.critical_sword.meta_data.ability.abilities

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.util.Util.halfRange
import com.github.w0819.critical_sword.util.Util.kill
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler
import com.github.w0819.critical_sword.util.manager.finder.EntityNearByNear
import com.github.w0819.critical_sword.util.unit.Radios
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.heartbeat.coroutines.Suspension
import io.github.monun.tap.config.Config
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

@Suppress("unused")
object Enma: SwordAbility(
    EnmaTask,
    { ingredient ->
        ingredient.type == Material.DRAGON_EGG
    }
) {

    @Config
    override var maxLevel: Int = 5

    override val eventManager: () -> SwordUseByUserHandler.AbilityEventManager<out SwordAbility> = { EnmaEventManager }

    @Config("searching-same-type-entity-radios-on-hit-entity")
    val searchRadios = Radios(
        500.0, 10.0, 500.0
    )

    @Config("strike-entity-lightning-on-hit-entity")
    var strikeLightningDelay = 500L

    @Config("breaking-deepness-on-swing")
    var breakingDeepness = 400

    @Config("width-of-breaking-area-on-swing")
    var xSize = 100

    @Config("height-of-breaking-area-on-swing")
    var ySize = 50

    private object EnmaTask: AbilityListener() {

        val onHitEntity: (EntityDamageByEntityEvent) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<Enma>) -> Unit = { event ->
            { player ->
                { _ ->
                    { levelStorage ->
                        val entity = event.damager

                        if (player is Player && entity is LivingEntity) {

                            HeartbeatScope().launch {
                                val suspension = Suspension()

                                EntityNearByNear.searchNearLivingEntity(
                                    player, entity, levelStorage, searchRadios
                                ).forEach { target ->
                                    val world = target.world

                                    target.run { kill(player) }.apply { world.strikeLightning(location) }

                                    suspension.delay(400L)
                                }
                            }
                        }
                    }
                }
            }
        }

        val onSwingSword: (PlayerInteractEvent) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<Enma>) -> Unit = { _ ->
            { player ->
                { _ ->
                    { levelStorage ->
                        if (player is Player) {
                            levelStorage.use { level ->
                                val playerLoc = player.location.toBlockLocation()
                                val playerDirection = playerLoc.direction
                                val cloneLoc = playerLoc.clone().add(playerDirection.clone().multiply(2.0))

                                val maxWidth = ((level * 0.1 + 1) * breakingDeepness).toInt()

                                val blocksToBreak = halfRange(xSize).map { distance ->
                                    cloneLoc.clone().add(
                                        playerDirection.clone().apply { y = 0.0 }.normalize().multiply(distance)
                                            .apply { x *= -1.0 }
                                    )
                                }.flatMap { locInLine ->
                                    halfRange(ySize).map { height ->
                                        locInLine.clone().apply { y += height }
                                    }
                                }.flatMap { locInSquare ->
                                    (0..maxWidth).map { width ->
                                        locInSquare.clone().add(playerDirection.clone().multiply(width))
                                    }
                                }.map(Location::getBlock)

                                blocksToBreak.forEach(Block::breakNaturally)
                            }
                        }
                    }
                }
            }
        }
    }

    object EnmaEventManager: SwordUseByUserHandler.AbilityEventManager<Enma>(
        Enma,
        SwordUseByUserHandler.AbilityTask(EnmaTask.onSwingSword, PlayerInteractEvent::class),
        SwordUseByUserHandler.AbilityTask(EnmaTask.onHitEntity, EntityDamageByEntityEvent::class)
    )

    init {
        simpleInstruction = "ability to strike a lightning and break on the ground"

        detail = """
            this ability do...
            when hit entity, strike a lightning on the same-type entities and
            when swing it, break blocks in square.
            
            the square to look for same-type entities is ${searchRadios.x} in width ${searchRadios.y} in height and ${searchRadios.z} in deepness
            the radius is bigger as much as level * 2 times then default
            
            the default size of square is $xSize in width and $ySize in height and $breakingDeepness in deepness.
            the width and deepness is bigger as much as level / 10 times then default.
        """.trimIndent()
    }
}