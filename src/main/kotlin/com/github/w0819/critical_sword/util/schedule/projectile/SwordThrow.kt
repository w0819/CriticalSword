package com.github.w0819.critical_sword.util.schedule.projectile

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.manager.event.ArmorItemGetter
import io.github.monun.tap.fake.*
import io.github.monun.tap.math.normalizeAndLength
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.random.Random

open class SwordThrow(
    private val thrower: Player, private val item: Sword
) : FakeProjectile(
    1200,
    thrower.clientViewDistance.toDouble()
) {

    private lateinit var swordArmor: FakeEntity<ArmorStand>

    companion object {
        val manager = FakeProjectileManager()
    }

    private fun summonThrowingArmor(direction: Vector, loc: Location): FakeEntity<ArmorStand> = SwordPlugin.fakeServer.spawnEntity(loc.clone(), ArmorStand::class.java).apply {
        updateMetadata {
            updateMetaThrowing(direction)
        }
    }

    fun setToLaunch(direction: Vector): SwordThrow {
        return apply { swordArmor = summonThrowingArmor(direction, thrower.location) }
    }

    private fun ArmorStand.updateMetaThrowing(direction: Vector): ArmorStand = apply {
        headPose = EulerAngle(-direction.x, direction.y - direction.y * 45/180, direction.z)
    }

    final override fun onMove(movement: Movement) {
        val to = movement.to
        swordArmor.apply { moveTo(to.clone().apply { y -= 1.62; yaw -= 90.0F; pitch -= 45f }) }

        swordArmor.updateMetadata { onCustomMoving() }
    }

    final override fun onTrail(trail: Trail) {
        trail.velocity?.let { v ->
            val from = trail.from
            val world = from.world

            val length = v.normalizeAndLength()

            if (length == 0.0) return

            world.rayTrace(
                from,
                v,
                range,
                FluidCollisionMode.NEVER,
                true,
                1.0,
                null
            )?.let { result ->
                val hitBlock = result.hitBlock
                val hitEntity = result.hitEntity

                if (hitEntity != null) if (hitEntity is LivingEntity) {
                    swordArmor.updateMetadata {
                        hitEntity.damage(item.damage, thrower)
                        customEntityHitEffect(hitEntity, item, thrower)
                    }
                } else if (hitBlock != null) swordArmor.updateMetadata {
                    val hitLoc = hitBlock.location

                    world.spawn(hitLoc.apply { y += 0.5 }, ArmorStand::class.java).also { spawn ->
                        spawn.headPose = headPose
                    }.also { armorStand ->
                        ArmorItemGetter.manage(armorStand, thrower)
                    }
                }
            }
        }
    }

    final override fun onRemove() {
        val armorStand = swordArmor.apply { remove() }

        armorStand.updateMetadata {
            onRemoveTask(item)
        }
    }
    open fun ArmorStand.onRemoveTask(sword: Sword) {
        val particleVector = Vector()

        repeat (64) { _ ->
            particleVector.copy(velocity).apply {
                x += Random.nextDouble() - 0.5
                y = Random.nextDouble()
                z += Random.nextDouble() - 0.5
            }
            world.spawnParticle(
                Particle.ITEM_CRACK,
                location,
                0,
                particleVector.x,
                particleVector.y,
                particleVector.z,
                1.0,
                item.vanilla
            )
        }
    }

    open fun ArmorStand.onCustomMoving() {}

    open fun ArmorStand.customEntityHitEffect(entity: LivingEntity, sword: Sword, cause: Player) {}
}