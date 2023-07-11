package com.github.w0819.critical_sword.util.manager.finder

import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import com.github.w0819.critical_sword.util.unit.Radios
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class EntityNearByNear<E: LivingEntity> private constructor(
    entities: List<E>
): ArrayList<E>(entities) {
    companion object {
        fun searchNearLivingEntity(centerEntity: Entity, target: LivingEntity, levelStorage: AbilityStorage.Level<*>, radios: Radios): EntityNearByNear<LivingEntity> {
            val centerLoc = centerEntity.location
            val world = centerEntity.world
            val type = target.type

            val sameTypeEntitiesAroundTarget = radios.use(levelStorage) { (xRadios, yRadios, zRadios) ->
                world.getNearbyLivingEntities(
                    centerLoc,
                    xRadios, yRadios, zRadios
                ).filter { acc ->
                    acc.type == type && (centerEntity.type != target.type || centerEntity.uniqueId != acc.uniqueId)
                }
            }
            val entities = sameTypeEntitiesAroundTarget.sortedBy { entity ->
                entity.location.distance(centerLoc)
            }

            return EntityNearByNear(entities)
        }
    }
}