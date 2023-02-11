package com.github.w0819.critical_sword.util.events

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.meta_data.Sword
import com.github.w0819.critical_sword.util.tracker.SwordTracker
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class TrackingCancelEvent(private val target: Sword): Event() {
    companion object {
        @JvmStatic
        private val handlers: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }
    override fun getHandlers(): HandlerList = getHandlerList()

    fun stopTracking() {
        SwordPlugin.debugMessage("tracking canceled by tracking completed")
        SwordTracker.cancelTasking(target)
    }

}