package com.github.w0819.critical_sword.util

import com.github.w0819.critical_sword.plugin.CriticalSword
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.pow

object Util  {
    val calculatorArea = { level: Int -> (level.toDouble() * 10).pow(2).toInt()}

    val Vanilla = listOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD
    )
    fun <T> List<T>.tail(): List<T> = if (isNotEmpty()) dropLast(1) else this

    val calculateLength:(level: Int) -> Double = { level ->
        when (level) {
            0 -> 0.0
            else -> (level * 10).toDouble()
        }
    }
    fun getEntitiesInArea(level: Int, player: Player): List<Entity> {
        val loc = player.location
        val world = loc.world

        return world.getNearbyLivingEntities(loc, calculateLength(level) , 256.0).toList()
    }


    /**
     * this makes plane with two points
     * @receiver it is a point of a line
     * @param loc is a point of a line either
     * @exception IllegalArgumentException if neither two points is not on a single line in case of plane
     * */
    operator fun Location.rangeTo(loc: Location): List<List<Location>> {
        require(x == loc.x || y == loc.y || z == loc.z)
        return (x.toInt()..loc.x.toInt()).map { x ->
            (z.toInt()..loc.z.toInt()).flatMap { z ->
                (y.toInt()..loc.y.toInt()).map { y ->
                    Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                }
            }
        }
    }

    /**
     * summon invisible armor stand in a line and breaking the blocks to move them
     * */

    fun breakingBlocksWithArmorStand(world: World, loc1: Location, loc2: Location, plugin: CriticalSword, deep: Double) {
        val slope = loc2.subtract(loc1).toVector() // vector to that armor going
        val armorLocs = (loc1.add(Location(world, -30.0, 0.0, 5.0))..loc1.add(Location(world, 30.0, 3.0, 5.0))) // range of that armor stands


        armorLocs.forEach {  sameXAndY ->
            sameXAndY.forEach { blockLoc ->
                val block = blockLoc.block
                block.breakNaturally()
            }
        }

        val armorStands  = armorLocs[0].map { armorLoc -> // ^-30 ^ ^5 ^30 ^3 ^5 air
            world.spawn(armorLoc, ArmorStand::class.java).apply {
                isVisible = false
                setCanMove(true)
                setGravity(false)
            }
        }
        val armorMoving: (ArmorStand) -> (Vector) -> Runnable = { stand ->
            { standVector ->
                Runnable {
                    stand.location.let { standLoc ->
                        val standWorld = standLoc.world
                        (standLoc..standLoc.add(Location(standWorld, 0.0, 5.0, 0.0))).flatten().forEach { it.block.breakNaturally() }
                        standLoc.add(standVector)
                    }
                }
            }
        }
        armorStands.forEach { armorStand ->
            val periodToArrive = deep / slope.length() + (deep % slope.length())
            plugin.bukkitScheduler.apply {
                val task  = runTaskTimer(plugin, armorMoving(armorStand)(slope), 0L, 1L)
                runTaskLater(plugin, Runnable { cancelTask(task.taskId) }, periodToArrive.toLong())
            }
        }
        // later it will be seperated into two pieces which part is breaking and the other part is damaging entity
        // for now, the damaging entity part is not available
    }

    inline fun <reified T: LivingEntity> killEntitiesAroundTheEntity(entity: T, width: Double, length: Double) {
        val world = entity.world
        val loc = entity.location
        val kill: (T) -> Unit = { livingEntity ->
            livingEntity.damage(livingEntity.health)
        }
        world.getNearbyLivingEntities(loc, width, length).filterIsInstance<T>().forEach(kill)
    }
}