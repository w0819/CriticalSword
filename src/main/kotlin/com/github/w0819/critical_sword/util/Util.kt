package com.github.w0819.critical_sword.util

import com.github.w0819.critical_sword.util.meta_data.Sword.Companion.isSwordType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object Util {
    val swords: List<Material> = listOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD
    )

    operator fun Location.rangeTo(other: Location): List<List<Location>> {
        require(world == other.world) { "received world and parameter's world are not equal, they will be equal!" }
        require(this != other) { "received location and parameter's one are equal, they will not be equal!" }
        return (x.toInt()..other.x.toInt()).flatMap { x ->
            (z.toInt()..other.z.toInt()).map { z ->
                (y.toInt()..other.y.toInt()).map { y ->
                    Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                }
            }
        }
    }

    operator fun Location.rangeUntil(other: Location): List<List<Location>> =
        other.rangeTo(this).reversed()

    fun Float.isBetween(a: Double, b: Double): Boolean = a < this && this > b

    fun killSameTypeEntityAround(entity: LivingEntity, xRadi: Double, yRadi: Double, zRadi: Double) {
        val world = entity.world
        val type = entity.type
        fun spreadOver(entity: Entity) {
            val loc = entity.location
            val entitiesAround = world.getNearbyEntitiesByType(type.entityClass,loc, xRadi, yRadi, zRadi)
            if (entitiesAround.isEmpty()) return
            val nearestEntity = entitiesAround.reduce { acc, other -> loc.run { if (distance(acc.location) >= distance(other.location)) other else acc } } as LivingEntity
            nearestEntity.apply { damage(health) }
            spreadOver(nearestEntity)
        }

        return spreadOver(entity)
    }

    fun Player.isHitWithSword(e: EntityDamageByEntityEvent): Boolean {
        val hitter = e.damager
        if (hitter == this && inventory.itemInMainHand.isSwordType()) return true
        return false
    }
}