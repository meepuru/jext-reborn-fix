package me.spartacus04.jext.listeners

import me.spartacus04.jext.JextState.ASSETS_MANAGER
import me.spartacus04.jext.JextState.BASE_URL
import me.spartacus04.jext.JextState.CONFIG
import me.spartacus04.jext.JextState.LANG
import me.spartacus04.jext.JextState.PLUGIN
import me.spartacus04.jext.language.LanguageManager.Companion.CROWDIN_LINK
import me.spartacus04.jext.language.LanguageManager.Companion.CROWDIN_MESSAGE
import me.spartacus04.jext.language.LanguageManager.Companion.UPDATE_LINK
import me.spartacus04.jext.listeners.utils.JextListener
import me.spartacus04.jext.utils.Updater
import me.spartacus04.jext.utils.sendJEXTMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

internal class PlayerJoinEvent : JextListener() {
    @EventHandler
    fun onPlayerJoin(playerJoinEvent: PlayerJoinEvent) {
        if (CONFIG.WEB_INTERFACE_API_ENABLED && CONFIG.RESOURCE_PACK_HOST) {
            val hostName = BASE_URL.getUrl(playerJoinEvent.player)

            val url = if (hostName.startsWith("https://") || hostName.startsWith("http://")) {
                // 当包含协议头时直接拼接路径
                "$hostName/resource-pack.zip"
            } else {
                // 原始拼接方式保留端口号
                "http://$hostName:${CONFIG.WEB_INTERFACE_PORT}/resource-pack.zip"
            }

            playerJoinEvent.player.setResourcePack(url, ASSETS_MANAGER.resourcePackHostedHash)
        }

        if (playerJoinEvent.player.hasPermission("jext.notifyupdate") && CONFIG.CHECK_FOR_UPDATES) {
            Updater().getVersion {
                if(PLUGIN.description.version == "dev") {
                    playerJoinEvent.player.sendMessage("Current upstream version is $it")
                } else if(it != PLUGIN.description.version) {
                    playerJoinEvent.player.sendJEXTMessage("update-available")
                    playerJoinEvent.player.sendMessage(UPDATE_LINK)
                }
            }
        }

        if(!LANG.hasLanguage(playerJoinEvent.player.locale)) {
            playerJoinEvent.player.sendMessage(CROWDIN_MESSAGE)
            playerJoinEvent.player.sendMessage(CROWDIN_LINK)
        }
    }
}