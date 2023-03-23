package com.github.w0819.critical_sword.util.unit

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

class EntityAndNearestEntity private constructor(val nearestEntity: LivingEntity) {
    companion object {
        fun defaultSearch(radios: Radios, target: Entity): List<EntityAndNearestEntity> {
            val world = target.world
            val loc = target.location
            val entitiesAround = world.getNearbyLivingEntities(loc, radios.x, radios.y, radios.z).filterIsInstance<LivingEntity>()

            return searchOver(entitiesAround, target, listOf())
        }

        fun searchByType(radios: Radios, target: Entity, type: EntityType): List<EntityAndNearestEntity> {
            val world = target.world
            val entitiesAround = world.getNearbyLivingEntities(target.location, radios.x, radios.y, radios.z).filter { entity -> entity.type == type }

            return searchOver(entitiesAround, target, listOf())
        }

        private tailrec fun searchOver(targetEntities: List<LivingEntity>,target: Entity, acc: List<EntityAndNearestEntity>): List<EntityAndNearestEntity> = when {
            targetEntities.isEmpty() -> acc
            else -> {
                val targetLoc = target.location
                val nearest = nearestEntity(targetLoc, targetEntities)
                val pair = EntityAndNearestEntity(nearest)
                searchOver(targetEntities - nearest, nearest, acc + pair)
            }
        }

        private fun <T: LivingEntity> nearestEntity(center: Location, targetEntities: List<T>): T = targetEntities.minByOrNull { targetEntity -> center.run { distance(targetEntity.location) } }!!
    }
}