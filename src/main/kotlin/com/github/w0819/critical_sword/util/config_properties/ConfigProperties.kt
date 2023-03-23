package com.github.w0819.critical_sword.util.config_properties

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util
import io.github.monun.tap.config.Config


object ConfigProperties {

    @Config
    var startToDebugging: Boolean = false

    fun load(plugin: SwordPlugin): Boolean = Util.compute(this,true,plugin)
}