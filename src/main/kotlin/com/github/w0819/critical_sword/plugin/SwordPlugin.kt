package com.github.w0819.critical_sword.plugin

import com.github.w0819.critical_sword.event.SwordEvent
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.util.config.ConfigUtil
import com.github.w0819.critical_sword.util.unit.Radios
import io.github.monun.tap.config.ConfigSupport
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin

class SwordPlugin: JavaPlugin() {

    companion object {
        lateinit var instance: SwordPlugin

        lateinit var fakeServer: FakeEntityServer

        val debuggingMessage: (String) -> Unit = { message ->
            if (ConfigUtil.startToDebugging) instance.server.logger.info(
                "[CriticalSword] $message\n"
            )
        }
    }

    override fun onEnable() {
        val event = SwordEvent()

        instance = this

        fakeServer = FakeEntityServer.create(this)

        server.scheduler.runTaskTimer(this, fakeServer::update, 0L, 1L)

        ConfigurationSerialization.registerClass(Radios::class.java)

        saveDefaultConfig()

        loadConfig(ConfigUtil)

        loadConfig(event)

        debuggingMessage("***warn**** you are in the debugging Mode what may make your console missy!")

        debuggingMessage("configs of event, ability and system were loaded")

        server.pluginManager.registerEvents(
            event, this
        )

        SwordAbility.abilities.forEach { ability ->
            ability.register(this)
        }

        debuggingMessage("plugin event was registered")

        debuggingMessage("the plugin loaded ${SwordAbility.abilities}")
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }

    fun loadConfig(target: Any): Boolean = ConfigSupport.compute(target,config, true).apply { saveConfig() }
}