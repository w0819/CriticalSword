package com.github.w0819.critical_sword.util

import com.github.w0819.critical_sword.meta_data.Sword.Companion.isSwordType
import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.unit.Radios
import io.github.monun.tap.config.ConfigSupport
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.pow

object Util {
    val swords: List<Material> = listOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD
    )

    operator fun Location.rangeTo(other: Location): List<List<Location>> {
        require(world == other.world) { "received world and parameter's world are not equal, they must!" }
        require(this != other) { "received location and parameter's one are equal, they must not!" }
        require(compareTo(other) == 0) { "received location is less than parameter's one, it must be bigger!" }
        return (x.toInt()..other.x.toInt()).flatMap { x ->
            (z.toInt()..other.z.toInt()).map { z ->
                (y.toInt()..other.y.toInt()).map { y ->
                    Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                }
            }
        }
    }

    operator fun Location.compareTo(other: Location): Int {
        val isXBigger = x >= other.x
        val isYBigger = y >= other.y
        val isZBigger = z >= other.z
        return when {
            isXBigger -> if (isZBigger) if (isYBigger) 0 else 1 else 2
            isZBigger -> if (isYBigger) 1 else 2
            isYBigger -> 2
            else -> 3
        }
    }


    operator fun Location.rangeUntil(other: Location): List<List<Location>> =
        other.rangeTo(this).reversed()

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

    fun Player.isHitWithSword(target: Entity): Boolean =
        target is LivingEntity && inventory.itemInMainHand.isSwordType()

    fun Location.getNearestLivingEntity(radios: Radios): LivingEntity? = world.getNearbyEntities(this, radios.x, radios.y, radios.z).fold<Entity?, LivingEntity?>(null) { acc: LivingEntity?, entity: Entity? ->
        when { // radios
            entity == null -> acc
            entity !is LivingEntity -> acc
            acc == null -> entity
            distance(entity.location) <= distance(acc.location) -> entity
            else -> acc
        }
    }

    fun Location.makeLookAtOther(tarLoc: Location): Location =
        apply {
            
            // default setting
            yaw = 0.0f
            pitch = 0.0f
            // yaw
            val yZeroVector = vectorTo(tarLoc.apply { y = 0.0 }) // y zero vector

            val distanceInPlane = (yZeroVector.x.pow(2) * yZeroVector.z.pow(2)).mod(2.0)
            val vectorToZIn2D = Vector(0.0,0.0,distanceInPlane) // the distance whether target is at 0 degree point
            val yawRate = vectorToZIn2D.divide(yZeroVector).length() // the degree of between fake entity location and target location

            yaw = (yawRate * 360.0).toFloat()
            // pitch

            val vectorIn3D = vectorTo(tarLoc)

            // pitchZeroVector is zero-pitch location and locToZ is ninety-pitch one and locToX is minus-ninety-pitch one

            pitch = if (vectorIn3D.y == 0.0) 0.0f else {
                val distance = distance(tarLoc)
                val straight = Vector(0.0, distance, 0.0)
                vectorIn3D.divide(straight).length().toFloat() + if (vectorIn3D.y < 0.0) 45.0f else 0.0f
            }
        }


    fun Location.vectorTo(other: Location): Vector = Vector(other.x - x, other.y - y, other.z - z)

    val widthLocation: (Int) -> (Location) -> (List<Location>) = { width ->
        { centerLoc ->
            val halfWidth = width.makeHalf()
            val distances = -halfWidth..halfWidth step 0.1
            distances.map { distance ->
                if (distance == 0.0) centerLoc else {
                    val degreeToRotate = if (distance > 0) 90.0f else -90.0f
                    val rotatedLoc = centerLoc.apply { yaw += degreeToRotate }
                    val vectorDistance = rotatedLoc.direction.multiply(distance / halfWidth)
                    centerLoc.add(vectorDistance)
                }
            }
        }
    }

    fun areaFromLoc1ToLoc2WithWidth(loc1: Location, centerLoc2: Location, totalWidth: Int): List<List<Location>> {
        val totalWidthLocation = widthLocation(totalWidth)

        val widthLocations1 = totalWidthLocation(loc1)
        val widthLocations2 = totalWidthLocation(centerLoc2)

        return widthLocations1.mapIndexed { index, loc ->
            val targetLoc = widthLocations2[index]
            val distanceWithTarget = targetLoc.distance(loc)
            val vector = loc.vectorTo(targetLoc)

            (1.0..distanceWithTarget).onePointStep().map { distance ->
                val pointDistanceVector = vector.multiply(distance/distanceWithTarget)
                loc.add(pointDistanceVector)
            }
        }
    }

    fun moveToOppsite(targetLoc: Location): Location = targetLoc.multiply(-1.0)

    fun nearestLivingEntityInArea(area: List<List<Location>>, maxDistance: Double, targetLoc: Location): LivingEntity? {
        val world = targetLoc.world
        val entities = world.getNearbyLivingEntities(targetLoc ,maxDistance, maxDistance, maxDistance)

        return entities.filter { entity -> entity.location in area.flatten() }.fold(null) { acc: LivingEntity?, entity: LivingEntity ->
            targetLoc.run {
                when (acc) {
                    null -> entity
                    else -> {
                        val distance1 = distance(entity.location)
                        val distance2 = distance(acc.location)
                        if (distance1 >= distance2) entity else acc
                    }
                }
            }
        }
    }

    fun areaFromPlayerToPlayerLooking(maxDistance: Int, player: Player, totalWidth: Int): List<List<Location>> {
        val loc = player.location
        val centerTargetLoc = player.getTargetBlock(null, maxDistance).location

        return areaFromLoc1ToLoc2WithWidth(loc, centerTargetLoc, totalWidth)

    }


    fun Int.makeHalf(): Double = div(2.0)

    fun ClosedRange<Double>.onePointStep(): Iterable<Double> = this step 0.1

    infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
        require(start.isFinite())
        require(endInclusive.isFinite())
        require(step > 0.0) { "Step must be positive, was: $step." }
        val sequence = generateSequence(start) { previous ->
            if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
            val next = previous + step
            if (next > endInclusive) null else next
        }
        return sequence.asIterable()
    }
    fun giveDamage(item: ItemStack, damage: Int): ItemStack = item.apply { itemMeta = itemMeta.apply { (this as org.bukkit.inventory.meta.Damageable).damage += damage } }

    fun LivingEntity.applyDamage(damage: Double): LivingEntity = apply { damage(damage) }

    fun LivingEntity.killEntity(): LivingEntity = applyDamage(health)

    fun Entity.strikeLightingEffect(): LightningStrike = world.strikeLightningEffect(location)

    fun Entity.strikeLighting(): LightningStrike = world.strikeLightning(location)


    fun divideToIntWithOutPoint(x: Double, y: Double): Int = (x / y + x % y).toInt()

    fun compute(target: Any, separateByClass: Boolean, plugin: SwordPlugin): Boolean = ConfigSupport.compute(target, plugin.config, separateByClass).apply { plugin.saveConfig() }

    fun failMessage(message: String): Component = Component.text(message).color(
        TextColor.color(255,0,0)
    )

    fun successMessage(message: String): Component = Component.text(message).color(
        TextColor.color(0,255,0)
    )
}