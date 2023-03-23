package com.github.w0819.critical_sword.plugin

import com.github.w0819.critical_sword.event.SwordEvent
import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.SwordAbility
import com.github.w0819.critical_sword.util.Util
import com.github.w0819.critical_sword.util.config_properties.ConfigProperties.load
import com.github.w0819.critical_sword.util.config_properties.ConfigProperties.startToDebugging
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class SwordPlugin: JavaPlugin() {

    lateinit var fakeServer: FakeEntityServer
        private set

    companion object {

        lateinit var instance: SwordPlugin

        @JvmStatic
        val debugMessage: (String) -> Unit = { message -> if (startToDebugging) instance.server.logger.info("[Critical_Sword] $message\n") }
    }
    override fun onEnable() {
        instance = this
        fakeServer = FakeEntityServer.create(this)

        saveDefaultConfig()


        debugMessage("loading config completed, there ${if (load(this)) "is not" else "is"} a change in config")

        debugMessage("***warn*** you are in debugging mode that may make your console missy")


        SwordAbility.abilities.forEach { ability ->
            debugMessage("completed loading properties of ${ability.name}, there ${if (ability.loadConfig(this)) "is" else "is not"} change in the properties")
        }

        if (startToDebugging) kommand {
            register("debugging") {
                then("give") {
                    then("player" to player()) {
                        then("type" to dynamicByMap(
                                Util.swords.fold(mapOf()) { acc: Map<String, Material>, material: Material ->
                                    acc + mapOf(material.name to material)
                                }
                            )
                        ) {
                            then("ability" to dynamicByMap(
                                SwordAbility.abilities.fold(mapOf()) { acc: Map<String, SwordAbility>, swordAbility ->
                                    acc + mapOf(swordAbility.name to swordAbility)
                                }
                            )
                            ) {
                                then("ability" to int(0)) {
                                    executes {
                                        val player: Player by it
                                        val type: Material by it
                                        val ability: SwordAbility by it
                                        val level: Int by it
                                        if (level > ability.maxLevel) {
                                            player.sendMessage(
                                                Util.failMessage(
                                                    "you are requesting $level for ability level but ability level cannot be higher than ${ability.maxLevel}"
                                                )
                                            )
                                            return@executes
                                        }

                                        val sword = Sword(ItemStack(type)).upgrade(ability)

                                        player.inventory.addItem(sword.vanilla)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        debugMessage("command ${if (startToDebugging) "was" else "was not"} loaded")


        server.pluginManager.registerEvents(SwordEvent(this), this@SwordPlugin)

        server.scheduler.runTaskTimer(this, fakeServer::update, 1L, 1L)

        server.logger.info("Critical Sword plugin was enabled")

    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }

    /*
    * 아 어떻게 하는 거였지?
    * 철, 다이야, 금, 나무, 돌 검을 들고 가운데 클릭을 하면 인밴토리가 열려서 능력을 바르고
    * 우클리을 하거나, 좌클릭을 해서 능력을 발동하는 것어었지!
    * - Emna
    *   우클릭 - 땅을 파고
    *   조클릭 - 몬스터를 때리거나 검을 날려서 몬스터를 죽인다
    * - Trident
    *   우클릭 -
    * */
}