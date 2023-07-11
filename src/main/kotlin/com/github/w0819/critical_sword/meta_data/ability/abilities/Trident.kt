package com.github.w0819.critical_sword.meta_data.ability.abilities

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.util.Util.step
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler.AbilityTask
import com.github.w0819.critical_sword.util.manager.finder.EntityNearByNear
import com.github.w0819.critical_sword.util.schedule.projectile.SwordThrow
import com.github.w0819.critical_sword.util.unit.Radios
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.heartbeat.coroutines.Suspension
import io.github.monun.tap.config.Config
import io.github.monun.tap.fake.FakeProjectileManager
import io.github.monun.tap.math.vector
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import kotlin.math.min

@Suppress("unused")
object Trident: SwordAbility(
    TridentTask,
    { ingredient ->
        ingredient.type == Material.TRIDENT
    }
) {
    @Config
    override var maxLevel: Int = 10

    override val eventManager: () -> SwordUseByUserHandler.AbilityEventManager<Trident> = { TridentEventManager }

    @Config("max-trident-spawn-amount-on-hit-entity")
    var amountOfTrident = 1

    @Config("tracking-entity-radios-on-hit-entity")
    var trackingEntityRadio = Radios(
        500.0, 100.0, 500.0
    )

    @Config("tracking-part-size-on-hit-entity")
    var movingTimes = 50

    @Config("tracking-sword-moving-delay-on-hit-entity")
    var trackingDelay = 10L

    @Config("max-block-of-flying-fake-sword-on-swing-sword")
    var maxFlyingDistance = 500




    object TridentTask: AbilityListener() {

        val onEntityDamageByEntity: (EntityDamageByEntityEvent) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<Trident>) -> Unit =
            { event ->
                { player ->
                    { sword ->
                        { levelStorage ->
                            val acc = event.damager // damager is hit by player

                            if (acc is LivingEntity && acc !is ArmorStand && player is Player) {
                                val loc = player.location

                                val sameTypeEntities = EntityNearByNear.searchNearLivingEntity(
                                    player, acc, levelStorage, trackingEntityRadio
                                )

                                val tridentSize = levelStorage.use<Int>(amountOfTrident) { tridentAmount ->
                                    min(tridentAmount, sameTypeEntities.size)
                                }

                                val times = sameTypeEntities.size / tridentSize

                                val rest = sameTypeEntities.size % tridentSize

                                val tridents = (1..tridentSize).map { i ->
                                    var turn = 1

                                    object : SwordThrow(player, sword) {
                                        override fun ArmorStand.onRemoveTask(sword: Sword) {
                                            if (turn <= times || i <= rest) {
                                                val target = sameTypeEntities[i * turn]
                                                val targetLoc = target.location
                                                val direction = location.vector(targetLoc).normalize()

                                                manager.launch(targetLoc, setToLaunch(direction))
                                            }

                                            if (tridentSize == i) turn++

                                        }
                                    }
                                }

                                tridents.forEachIndexed { index, p ->
                                    SwordThrow.manager.launch(
                                        loc,
                                        p.setToLaunch(
                                            loc.vector(sameTypeEntities[index].location)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

        private const val projectileForTridentTag = "is_for_trident"

        val onPlayerInteract: (PlayerInteractEvent) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<Trident>) -> Unit =
            { event ->
                { player ->
                    { sword ->
                        { level ->
                            val action = event.action

                            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                                if (player is Player) {
                                    val tridentProjectile = object : SwordThrow(
                                        player, sword
                                    ) {
                                        override fun ArmorStand.customEntityHitEffect(
                                            entity: LivingEntity,
                                            sword: Sword,
                                            cause: Player
                                        ) {
                                            val suspension = Suspension()

                                            HeartbeatScope().launch {
                                                suspension.delay(100L)

                                                val radian = level * 4.0 // blocks
                                                val speed = level * 6.0 // block per second
                                                val height = entity.boundingBox.centerY

                                                val directions: List<Vector> = (-radian..radian step 0.5).flatMap { x ->
                                                    (-radian..radian step 0.5).flatMap { y ->
                                                        (-radian..radian step 0.5).map { z ->
                                                            Vector(x, y, z)
                                                        }
                                                    }
                                                }.map { vector -> vector.apply { y += height }.multiply(radian) }

                                                val spawnLocs = directions.associateTo(mutableMapOf()) { vector ->
                                                    vector.multiply(-1 * speed) to entity.location.add(vector)
                                                }

                                                spawnLocs.map { (vector, loc) ->
                                                    val subProjectile = SwordThrow(
                                                        player, sword
                                                    )

                                                    FakeProjectileManager().launch(loc, subProjectile).apply {
                                                        velocity = vector
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    val loc = player.location

                                    level.use { i ->
                                        val vector = loc.direction.multiply(i)
                                        SwordThrow.manager.launch(loc, tridentProjectile.setToLaunch(vector))
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

    object TridentEventManager: SwordUseByUserHandler.AbilityEventManager<Trident>(
        Trident,
        AbilityTask(TridentTask.onPlayerInteract, PlayerInteractEvent::class),
        AbilityTask(TridentTask.onEntityDamageByEntity, EntityDamageByEntityEvent::class)
    )

    init {
        simpleInstruction = "ability to poke hit entity with tridents surrounded or launched"

        detail = """
            this ability do...
            when hit Entity, spawn tridents around entity
            when swing sword, launch one tridents and when this trident poke entity, ability do as upon have done
            
            the spawn radius is level * 4 and the spawn amount is level * 1
        """.trimIndent()
    }
}