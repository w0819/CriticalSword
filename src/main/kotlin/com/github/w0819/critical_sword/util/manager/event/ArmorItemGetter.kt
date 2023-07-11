package com.github.w0819.critical_sword.util.manager.event

import org.bukkit.NamespacedKey
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object ArmorItemGetter {
    private val getterManagedTag = NamespacedKey.minecraft("getter_manager_tag")

    private val ownerTag = NamespacedKey.minecraft("player_who_can_pick_up_item")

    fun manage(armorStand: ArmorStand, owner: Player) {
        val scoreboardTag = armorStand.scoreboardTags
        if (getterManagedTag.key !in scoreboardTag) {
            armorStand.apply { addScoreboardTag(getterManagedTag.key) }.apply {
                persistentDataContainer[ownerTag, PersistentDataType.STRING] = owner.uniqueId.toString()
            }
        }
    }

    fun isManaged(armorStand: ArmorStand): Boolean = getterManagedTag.key in armorStand.scoreboardTags

    fun Player.canPickUpArmorItem(inHere: ArmorStand): Boolean {
        require(isManaged(inHere))

        return inHere.persistentDataContainer[ownerTag, PersistentDataType.STRING] == uniqueId.toString()
    }
}