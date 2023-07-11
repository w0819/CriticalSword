package com.github.w0819.critical_sword.util

import com.github.w0819.critical_sword.util.unit.XYZ
import io.github.monun.heartbeat.coroutines.Suspension
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance

object Util {
    val halfRange: (Int) -> IntRange = { size ->
        -(size / 2)..size / 2
    }

    fun String.toNamespacedKey(): NamespacedKey {
        val lowerCase = lowercase()
        val key = lowerCase.fold("") { acc, c ->
            acc + if (c == ' ') '_' else c
        }

        return NamespacedKey.minecraft(key)
    }

    fun successMessage(message: String): Component = Component.text(message).color(TextColor.color(0, 255, 0))
    fun failMessage(message: String): Component = Component.text(message).color(TextColor.color(255,0,0))

    @JvmStatic
    fun load(pkg: String): List<Any> {
        val jarFile = JarFile(this::class.java.protectionDomain.codeSource.location.path)
        val entries = jarFile.entries()
        return entries.toList().map {
            it.name
        }.filter {
            it.startsWith("$pkg.".replace(".", "/")) && it.endsWith(".class")
        }.map {
            Class.forName(it.replace("/", ".").removeSuffix(".class"))
        }.filter { clazz ->
            !clazz.isSynthetic && !clazz.isAnonymousClass && !clazz.isEnum && !clazz.isInterface && !clazz.isMemberClass
        }.map { clazz -> clazz.kotlin}.filter { kClass ->
            !kClass.isCompanion && !kClass.isAbstract && !kClass.isOpen && !kClass.isFun && kClass.isFinal
        }.map { kClass ->
            kClass.objectInstance ?: kClass.createInstance()
        }
    }

    infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
        require(start.isFinite())
        require(endInclusive.isFinite())
        require(step > 0.0) { "Step must be positive, was: $step." }
        val sequence = generateSequence(start) { previous ->
            if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
            val next = previous + step
            if (next > endInclusive) null else next
        }
        return sequence.asIterable()
    }

    fun LivingEntity.kill(by: Entity): LivingEntity = apply { damage(health, by) }

    suspend fun <T> waiting(times: Int,wait: Long,target: T,p: (T) -> Boolean, task: ((Int) -> Unit)? = null): Boolean {
        tailrec suspend fun waiting(done: Int): Boolean = if (done != times) {
            if (!p(target)) false else {
                task?.let { it(done) }.run { Suspension().delay(wait) }
                waiting(done + 1)
            }
        } else true

        return waiting(0)
    }

    fun getNearbyEntitiesByTypeInRadios(world: World, loc: Location, triple: XYZ) = triple.let { (xRadios, yRadios, zRadios) -> world.getNearbyEntitiesByType(Item::class.java,loc ,xRadios, yRadios, zRadios) }.toList()

    fun Any.simpleName(): String = this::class.simpleName ?: ""
}