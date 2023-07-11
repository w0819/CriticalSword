package com.github.w0819.critical_sword.util.config

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.Name

@Name("config-properties")
object ConfigUtil {

    @Config
    var startToDebugging: Boolean = false
}