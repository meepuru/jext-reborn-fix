package me.spartacus04.jext.listener

import me.spartacus04.jext.config.ConfigData.Companion.CONFIG
import me.spartacus04.jext.disc.DiscContainer
import me.spartacus04.jext.disc.DiscPlayer
import me.spartacus04.jext.integrations.IntegrationsRegistrant
import me.spartacus04.jext.jukebox.JukeboxContainer
import me.spartacus04.jext.jukebox.legacy.LegacyJukeboxContainer
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Jukebox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

internal class JukeboxEventListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onJukeboxInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return

        if (event.action != Action.RIGHT_CLICK_BLOCK || block.type != Material.JUKEBOX) return

        when(CONFIG.JUKEBOX_BEHAVIOUR) {
            "legacy-gui" -> legacyJukeboxGui(event, block)
            "gui" -> jukeboxGui(event, block)
            else -> defaultBehaviour(event, block)
        }
    }

    private fun defaultBehaviour(event: PlayerInteractEvent, block: Block) {
        if(!IntegrationsRegistrant.hasJukeboxAccess(event.player, block)) return

        val state = block.state as? Jukebox ?: return
        val location = block.location

        if(state.record.type == Material.AIR) {
            try {
                val disc = event.item ?: return
                val discContainer = DiscContainer(disc)

                discContainer.play(location)
            } catch (_: IllegalStateException) { }
        }
        else {
            try {
                val disc = state.record
                val discContainer = DiscContainer(disc)

                DiscPlayer.stop(location, discContainer.namespace)
            } catch (_: IllegalStateException) { }
        }
    }

    private fun legacyJukeboxGui(event: PlayerInteractEvent, block: Block) {
        event.isCancelled = true

        if(!IntegrationsRegistrant.hasJukeboxGuiAccess(event.player, block)) return

        LegacyJukeboxContainer.get(plugin, block.location).open(event.player)
    }

    private fun jukeboxGui(event: PlayerInteractEvent, block: Block) {
        event.isCancelled = true

        if(!IntegrationsRegistrant.hasJukeboxGuiAccess(event.player, block)) return

        JukeboxContainer(event.player, block)
    }

    @EventHandler(ignoreCancelled = true)
    fun onJukeboxBreak(event: BlockBreakEvent) {
        val loc = event.block.location

        LegacyJukeboxContainer.get(plugin, loc).breakJukebox()

        val block = event.block
        val state = block.state as? Jukebox ?: return

        try {
            val disc = state.record
            val discContainer = DiscContainer(disc)

            DiscPlayer.stop(loc, discContainer.namespace)
        } catch (_: IllegalStateException) { }
    }


}