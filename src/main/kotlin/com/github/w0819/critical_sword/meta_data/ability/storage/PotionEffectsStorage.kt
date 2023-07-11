package com.github.w0819.critical_sword.meta_data.ability.storage

import com.github.w0819.critical_sword.meta_data.ability.storage.PotionEffectsStorage.PotionType.Companion.invoke
import com.github.w0819.critical_sword.meta_data.ability.storage.PotionEffectsStorage.PotionType.Companion.isPotionType
import com.github.w0819.critical_sword.util.ListUtil
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PotionEffectsStorage(
    sword: ItemStack,
): SwordDataStorage<PotionEffectsStorage.PotionType, PotionEffectsStorage.EffectStorage>(
    sword, NamespacedKey.minecraft("potion_effect_storage"),
    EffectStorage::class,
    PotionEffectType.values().flatMap { type ->
        ListUtil.potionMaterials.map { material ->
             invoke(material, type)
         }
    },
    2
) {

    private val activePotionEffects = PotionEffectType.values().toList().flatMap { type ->
        listOf(
            Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION
        ).mapNotNull { material ->
            get(PotionType(material, type))
        }
    }
    val defaultPotionEffects = activePotionEffects.filter { storage -> storage.type is PotionType.Potion }

    val splashPotionEffects = activePotionEffects.filter { storage -> storage.type is PotionType.SplashPotion}

    val lingeringPotionEffects = activePotionEffects.filter { storage -> storage.type is PotionType.LingeringPotion }

    fun setPotionEffect(potionItem: ItemStack) {
        require(potionItem.isPotionType())

        val potionMeta = potionItem.itemMeta as PotionMeta

        if (potionMeta.hasCustomEffects()) {

            potionMeta.customEffects.forEach { effect ->
                val type = invoke(potionItem.type, effect.type)

                val set: (Int) -> (Int) -> Unit =
                    { duration ->
                        { amplifier ->
                            set(type, duration, amplifier)
                        }
                    }
                get(type)?.effect?.let { storageEffect ->
                    val setter = when {
                        effect.duration > storageEffect.duration -> set(effect.duration)
                        else -> set(storageEffect.duration)
                    }

                    when {
                        effect.amplifier > storageEffect.amplifier -> {
                            setter(effect.amplifier)
                        }
                        else -> setter(storageEffect.amplifier)
                    }
                }
            }

            updateToSword()
        }
    }

    class EffectStorage(val type: PotionType, duration: Int, amplifier: Int) {
        val effect = PotionEffect(type.potionType, duration, amplifier, true, true)
    }

    sealed class PotionType(private val material: Material, val potionType: PotionEffectType) {

        class Potion(type: PotionEffectType): PotionType(Material.POTION, type)

        class SplashPotion(type: PotionEffectType): PotionType(Material.SPLASH_POTION, type)

        class LingeringPotion(type: PotionEffectType): PotionType(Material.LINGERING_POTION, type)


        override fun toString(): String {
            return "${material.toString().lowercase()}_${potionType.toString().lowercase()}"
        }


        companion object {
            operator fun invoke(potion: Material, type: PotionEffectType): PotionType {
                require(potion in ListUtil.potionMaterials) { "wrong type of potion: $potion" }

                return when(potion) {
                    Material.POTION -> Potion(type)
                    Material.SPLASH_POTION -> SplashPotion(type)
                    Material.LINGERING_POTION -> LingeringPotion(type)
                    else ->throw RuntimeException("non-reachable")
                }
            }

            fun ItemStack.isPotionType(): Boolean = type in ListUtil.potionMaterials
        }
    }
}