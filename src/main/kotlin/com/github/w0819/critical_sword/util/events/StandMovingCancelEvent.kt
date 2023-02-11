package com.github.w0819.critical_sword.util.events

import com.github.w0819.critical_sword.util.tracker.StandMover
import org.bukkit.entity.ArmorStand
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class StandMovingCancelEvent(private val stands: List<ArmorStand>): Event() {

    companion object {
        @JvmStatic
        private val handlers: HandlerList = HandlerList()

        @JvmStatic
        private fun getHandlerList(): HandlerList = handlers
    }
    override fun getHandlers(): HandlerList = getHandlerList()

    fun cancelMoving(): Boolean = StandMover.findMovingStand(stands)?.cancelMoving() != null
}