package com.github.w0819.critical_sword.util

import org.bukkit.Material

object ListUtil {
    val swordTypes = listOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD
    )

    val primitiveTypeNames = listOf(
        Boolean::class, String::class, Short::class,
        Int::class, IntArray::class, Long::class, LongArray::class,
        Double::class, Float::class, Byte::class, ByteArray::class
    ).map { clazz -> clazz.simpleName ?: "" }

    val potionMaterials = listOf(
        Material.POTION,
        Material.SPLASH_POTION,
        Material.LINGERING_POTION
    )
}