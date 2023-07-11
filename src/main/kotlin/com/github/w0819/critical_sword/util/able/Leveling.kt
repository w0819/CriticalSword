package com.github.w0819.critical_sword.util.able

import com.github.w0819.critical_sword.meta_data.ability.SwordAbility
import com.github.w0819.critical_sword.meta_data.ability.storage.AbilityStorage

interface Leveling<T>: Multivariable<T> {
    operator fun times(levelStorage: AbilityStorage.Level<SwordAbility>): T

    fun <U> use(levelStorage: AbilityStorage.Level<SwordAbility>, useTask: (T) -> U): U = useTask(times(levelStorage))
}