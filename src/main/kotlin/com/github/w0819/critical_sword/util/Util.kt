package com.github.w0819.critical_sword.util

import org.bukkit.Material
import kotlin.math.pow

object Util  {
    val calculatorArea = {level: Int -> (level.toDouble() * 10).pow(2).toInt()}

    val Vanilla = listOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD
    )

}