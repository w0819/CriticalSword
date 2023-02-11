package com.github.w0819.critical_sword.util.tracker


import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.events.StandMovingCancelEvent
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

class StandMover private constructor(val armorStands: List<ArmorStand>,private val task: BukkitTask){
    companion object {
        @JvmStatic
        private val movingStands: MutableList<StandMover> = mutableListOf()

        @JvmStatic
        fun create(player: Player, deep: Double, plugin: SwordPlugin) {
            val start = player.location
            val target = player.getTargetBlock(null, 10)
            val unitSpeed = Vector(target.x - start.x, target.y - start.y, target.z - start.z)
            val during = deep / unitSpeed.length()
            val world = player.world

            val centerStand = world.spawn(target.location, ArmorStand::class.java)
            val extraStands = centerStand.location.spawnInALine(listOf(
                -2, -1, 1, 2
            ))
            val totalStands = extraStands + centerStand

            val run: (List<ArmorStand>) -> (Vector) -> (Location) -> Runnable = { targetStands ->
                { speed ->
                    { reach ->
                        Runnable {
                            targetStands.forEach { stand ->
                                if (reach != stand.location) {
                                    stand.location.apply {
                                        block.breakNaturally()
                                        add(speed)
                                    }
                                } else plugin.server.pluginManager.callEvent(StandMovingCancelEvent(targetStands))
                            }
                        }
                    }
                }
            }

            startTaskAndUpload(plugin,totalStands,run(totalStands)(unitSpeed)(target.location.add(unitSpeed.multiply(during))))
        }

        @JvmStatic
        private fun Location.spawnInALine(eachDistances: List<Int>): List<ArmorStand> {

            return eachDistances.map { distance ->
                world.spawn(
                    add(x + distance * (1 - yaw), 0.0, z + distance * (yaw - 1)),
                    ArmorStand::class.java
                )
            }
        }

        fun findMovingStand(stands: List<ArmorStand>): StandMover? = movingStands.find { mover -> mover.armorStands == stands }

        private fun startTaskAndUpload(plugin: SwordPlugin, armorStands: List<ArmorStand>, runnable: Runnable) {
            val task = plugin.server.scheduler.runTaskTimer(plugin, runnable, 1L, 1L)
            StandMover(armorStands, task).uploadOn()
        }
    }

    fun cancelMoving() {
        task.cancel()
        movingStands.remove(this)
        armorStands.forEach(ArmorStand::remove)
    }

    fun uploadOn() = movingStands.add(this)
}
