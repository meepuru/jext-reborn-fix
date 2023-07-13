package me.spartacus04.jext.jukebox

import me.spartacus04.jext.config.ConfigData
import me.spartacus04.jext.disc.DiscContainer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class JukeboxEntry(
    var type: String,
    var value: String
) {
    fun toItemStack() : ItemStack {
        return if(type == "jext") {
            val disc = ConfigData.DISCS.first { it.DISC_NAMESPACE == value }

            DiscContainer(disc).discItem
        } else {
            val material = Material.matchMaterial(value)

            if(material != null) {
                return ItemStack(material)
            }

            arrayListOf(
                DiscContainer.SOUND_MAP.keys.map { mat -> ItemStack(mat) },
                ConfigData.DISCS.map { disc -> DiscContainer(disc).discItem }
            ).flatten().random()
        }
    }
}