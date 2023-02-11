package com.github.w0819.critical_sword.util.config_properties

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

object ConfigProperties {
    private val configFile = File("/resource/config.yml")

    @Config
    var isPrintDebugMessage: Boolean = false

    fun load(config: FileConfiguration): Boolean =
        ConfigSupport.compute(config, configFile, true)

}