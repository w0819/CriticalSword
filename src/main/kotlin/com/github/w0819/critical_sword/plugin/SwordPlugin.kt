package com.github.w0819.critical_sword.plugin

import com.github.w0819.critical_sword.event.SwordEvent
import com.github.w0819.critical_sword.util.config_properties.ConfigProperties
import com.github.w0819.critical_sword.util.config_properties.ConfigProperties.isPrintDebugMessage
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*

class SwordPlugin: JavaPlugin() {

    companion object {

        lateinit var instance: SwordPlugin

        @JvmStatic
        val debugMessage: (String) -> Unit = { message -> if (isPrintDebugMessage) instance.server.logger.info("[Critical_Sword] $message\n") }
    }
    override fun onEnable() {
        instance = this

        saveDefaultConfig()
        val hasChanged = ConfigProperties.load(config)
        debugMessage("loading config completed, there ${if (hasChanged) "is not" else "is"} a change in config")

        server.pluginManager.registerEvents(SwordEvent(this), this@SwordPlugin)

        kommand {
            register("test") {
                then("vector") {
                    then("x" to double()) {
                        then("y" to double()) {
                            then("z" to double()) {
                                then("entityType" to dynamicByEnum(EnumSet.allOf(EntityType::class.java))) {
                                    executes {
                                        val x: Double by it
                                        val y: Double by it
                                        val z: Double by it
                                        val vector = Vector(x,y,z)
                                        val entityType: EntityType by it

                                        world.spawnEntity(location, entityType).apply { location.add(vector) }
                                    }
                                }
                            }
                        }
                    }
                }
                then("isItPrintingDebugMessage") {
                    executes {
                        server.logger.info("server is ${if (!isPrintDebugMessage) "not" else ""} printing debug messages")
                    }
                }
            }
        }
        debugMessage("command was loaded")

        server.logger.info("Critical Sword plugin was enabled")

    }



}