package com.github.w0819.critical_sword.util.manager.event

import com.github.w0819.critical_sword.meta_data.Sword
import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage
import io.papermc.paper.event.entity.EntityDamageItemEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.reflect.KClass
import kotlin.reflect.cast

object SwordUseByUserHandler {
    val providedEvents: List<KClass<out Event>> = listOf(
        PlayerInteractEvent::class,
        EntityDamageItemEvent::class,
        EntityDamageByEntityEvent::class,
        ProjectileHitEvent::class
    )

    inline fun <reified E: Event> call(event: E, user: LivingEntity,sword: Sword): Boolean {
        val havingAbilities = sword.storage.havingAbilities

        havingAbilities.forEach { (ability, _) ->
            val eventManager = ability.eventManager()
            eventManager.callTask(event, user, sword)
        }

        return havingAbilities.isNotEmpty()
    }

    abstract class AbilityEventManager<T: SwordAbility>(val ability: T, private vararg val tasks: AbilityTask<out Event, T>) {
        fun <T: Event> callTask(e: T, user: LivingEntity, sword: Sword): Boolean =
            tasks.filter { abilityTask -> abilityTask.isForRun(e) }.onEach { task -> task.conductTask(e, user, sword, ability) }.isNotEmpty()
    }

    /**
     * @exception IllegalArgumentException if [classType] is not in [providedEvents]
     * */
    class AbilityTask<E: Event, U: SwordAbility> private constructor(private val run: (E) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<U>) -> Unit,private val classType: KClass<E>) {
        companion object {
            operator fun <E: Event, U: SwordAbility> invoke(run: (E) -> (LivingEntity) -> (Sword) -> (AbilityStorage.Level<U>) -> Unit, classType: KClass<E>): AbilityTask<E, U> {
                require(classType in providedEvents) { "${classType.simpleName} is not provided!" }

                return AbilityTask(run, classType)
            }
        }

        fun <T: Event> isForRun(e: T): Boolean = e::class in providedEvents && classType.isInstance(e)

        fun <T: Event> conductTask(e: T, user: LivingEntity, sword: Sword, ability: U) {
            require(isForRun(e)) { "${e::class.simpleName} is not ${classType.simpleName}" }

            sword.storage.get<U>(ability).let { levelStorage -> run(classType.cast(e))(user)(sword)(levelStorage) }
        }
    }
}