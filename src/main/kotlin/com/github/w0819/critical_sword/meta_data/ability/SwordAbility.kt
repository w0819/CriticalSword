package com.github.w0819.critical_sword.meta_data.ability

import com.github.w0819.critical_sword.plugin.SwordPlugin
import com.github.w0819.critical_sword.util.Util.load
import com.github.w0819.critical_sword.util.Util.simpleName
import com.github.w0819.critical_sword.util.Util.toNamespacedKey
import com.github.w0819.critical_sword.util.manager.event.SwordUseByUserHandler
import com.github.w0819.critical_sword.util.unit.HowFit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class SwordAbility(
    private val listener: AbilityListener,
    vararg options: (ItemStack) -> Boolean
) {

    lateinit var simpleInstruction: String

    lateinit var detail: String

    val key: NamespacedKey = toString().toNamespacedKey()

    val isIngredients: () -> HowFit<ItemStack, SwordAbility> = { HowFit(this, *options) }

    abstract var maxLevel: Int
        internal set

    companion object {
        val abilities: List<SwordAbility> =
            load("com.github.w0819.critical_sword.meta_data.ability.abilities").filterIsInstance<SwordAbility>()

        val isIngredient: (List<ItemStack>) -> Boolean =
            { ingredients ->
                abilities.any { ability -> ability.isIngredients().isAllFit(ingredients) }
            }

        val toAbility: (List<ItemStack>) -> List<SwordAbility> = { ingredients ->
            require(isIngredient(ingredients))

            val ingredient = abilities.map { ability -> ability.isIngredients() }.filter { howFit ->
                howFit.isAllFit(ingredients)
            }

            val fittest = ingredient.maxOfOrNull { it.sizeToFit }

            ingredient.filter { howFit -> howFit.sizeToFit == fittest }.map { it.value }
        }
    }

    fun register(plugin: SwordPlugin) {
        plugin.loadConfig(this@SwordAbility)
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    final override fun toString(): String = simpleName().lowercase()

    abstract val eventManager: () -> SwordUseByUserHandler.AbilityEventManager<out SwordAbility>

    abstract class AbilityListener : Listener {
        val plugin = SwordPlugin.instance
        val fakeServer = SwordPlugin.fakeServer
        val scheduler = plugin.server.scheduler
    }
}