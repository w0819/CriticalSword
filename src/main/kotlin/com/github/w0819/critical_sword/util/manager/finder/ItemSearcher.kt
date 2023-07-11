package com.github.w0819.critical_sword.util.manager.finder

import com.github.w0819.critical_sword.util.Util.getNearbyEntitiesByTypeInRadios
import com.github.w0819.critical_sword.util.Util.waiting
import com.github.w0819.critical_sword.util.unit.XYZ
import io.github.monun.heartbeat.coroutines.HeartbeatScope
import io.github.monun.heartbeat.coroutines.Suspension
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Item


class ItemSearcher (
    private val searchMaxTry: Int,
    private val searchDelay: Long,
    private val triple: XYZ,
    private val wantCasting: Boolean = false,
    private val casting: Int = 0,
    private val castingDelay: Long = 0,
    private val castingEffect: ((World) -> (Location) -> (Int) -> Unit)? = null
) {
    val getNearbyEntitiesByTypeInRadios: (Item) -> List<Item> = { item ->
        getNearbyEntitiesByTypeInRadios(item.world, item.location, triple)
    }

    fun <T> itemSearch(target: Item,checker: (List<Item>) -> Boolean, mapper: (List<Item>) -> T,afterCastingTask: (Item) -> (T) -> Unit) {
        HeartbeatScope().launch {
            suspend fun itemSearch(triedTime: Int) {
                val checkWithoutRemoved: (List<Item>) -> Boolean = { targetItems -> checker(targetItems.filter(Item::isValid)) }

                if (triedTime != searchMaxTry) {
                    val items = getNearbyEntitiesByTypeInRadios(target)

                    if (checkWithoutRemoved(items)) {
                        val afterTask: (T) -> Unit = afterCastingTask(target)

                        if (wantCasting) {
                            val castingEffectTask: ((Int) -> Unit)? = castingEffect?.let { it(target.world)(target.location) }
                            casting(items, checkWithoutRemoved, mapper, afterTask, castingEffectTask)
                        } else afterTask(mapper(items))
                    } else {
                        Suspension().delay(searchDelay)
                        itemSearch(triedTime + 1)
                    }
                }
            }

            itemSearch(0)
        }
    }

    private suspend fun <T> casting(targetItems: List<Item>, checker: (List<Item>) -> Boolean, mapper: (List<Item>) -> T, afterCastingTask: (T) -> Unit, castingTask: ((Int) -> Unit)?) {
        if (waiting(casting, castingDelay, targetItems, checker, castingTask)) {
            afterCastingTask(mapper(targetItems))
        }
    }
}
