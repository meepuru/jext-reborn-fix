package me.spartacus04.jext.utils

import me.spartacus04.jext.JextState
import me.spartacus04.jext.JextState.ASSETS_MANAGER
import me.spartacus04.jext.JextState.CONFIG
import org.bukkit.entity.Player

fun sendResourcePack(player: Player) {
    if (CONFIG.WEB_INTERFACE_API_ENABLED && CONFIG.RESOURCE_PACK_HOST) {
        val hostName = JextState.BASE_URL.getBaseUrl(player)

        val url = if (hostName.startsWith("https://") || hostName.startsWith("http://")) {
            // 当包含协议头时直接拼接路径
            "$hostName/resource-pack.zip"
        } else {
            // 原始拼接方式保留端口号
            "http://$hostName:${CONFIG.WEB_INTERFACE_PORT}/resource-pack.zip"
        }

        player.setResourcePack(url, ASSETS_MANAGER.resourcePackHostedHash)
    }
}