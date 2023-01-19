package com.github.w0819.critical_sword.plugin

import com.github.w0819.critical_sword.event.SwordEvent
import org.bukkit.plugin.java.JavaPlugin
import com.github.w0819.critical_sword.sword.meta_data.SwordAbility
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.scheduler.BukkitScheduler

class CriticalSword : JavaPlugin() {
    lateinit var bukkitScheduler: BukkitScheduler
        private set
    lateinit var fakeServer: FakeEntityServer
        private set

    companion object {
        @JvmStatic
        lateinit var instance: CriticalSword
            private set
    }

    override fun onEnable() {
        instance = this
        fakeServer = FakeEntityServer.create(this)
        bukkitScheduler = instance.server.scheduler
        bukkitScheduler.apply {
            runTaskTimer(this@CriticalSword, fakeServer::update, 0L, 1L)
        }

        server.pluginManager.registerEvents(SwordEvent(instance), instance)

        SwordAbility.apply {
            BREAKING.register()
            FIRING.register()
            EXPLODING.register()
            LIGHTING.register()
        }


        server.logger.info("the plugin for Critical sword is enabled!")
    }
}