package me.spartacus04.jext.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.spartacus04.jext.config.ConfigData.Companion.CONFIG
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.collections.HashMap


class LanguageManager(private val autoMode : Boolean, private val plugin: JavaPlugin) {
    private val loadedLanguageMap = HashMap<String, Map<String, String>>()

    private var gson : Gson = GsonBuilder().setLenient().setPrettyPrinting().create()

    init {
        val path = "langs"

        JarFile(File(javaClass.protectionDomain.codeSource.location.path).absolutePath.replace("%20", " ")).use {
            val entries: Enumeration<JarEntry> = it.entries() //gives ALL entries in jar
            while (entries.hasMoreElements()) {
                val element = entries.nextElement()
                if (element.name.startsWith("$path/") && element.name.endsWith(".json")) {
                    val langName = element.name.replaceFirst("$path/", "")

                    plugin.getResource("langs/$langName")!!.bufferedReader().use {file ->
                        val mapType = object : TypeToken<Map<String, String>>() {}.type
                        val languageMap : Map<String, String> = gson.fromJson(file.readText(), mapType)

                        loadedLanguageMap.put(langName.replace(".json", "").lowercase(), languageMap)
                    }
                }
            }
        }

        if(!autoMode) {
            // Lets the server owner set a custom file
            val customFile = plugin.dataFolder.resolve("lang.json")

            if (!customFile.exists()) {
                plugin.getResource("langs/en_US.json")!!.bufferedReader().use {
                    customFile.createNewFile()
                    customFile.writeText(it.readText())
                }
            }

            ConfigVersionManager.updateLang(customFile, loadedLanguageMap["en_us"]!!)
            customFile.bufferedReader().use {
                val mapType = object : TypeToken<Map<String, String>>() {}.type
                val languageMap : Map<String, String> = gson.fromJson(it.readText(), mapType)

                loadedLanguageMap.put("custom", languageMap)
            }
        }
    }

    fun getString(commandSender: CommandSender, key : String) : String {
        if(!autoMode) {
            return loadedLanguageMap["custom"]!![key]!!
        }

        val locale = if (commandSender is Player) {
            commandSender.locale
        }
        else {
            "en_us"
        }

        return if(loadedLanguageMap.containsKey(locale)) {
            loadedLanguageMap[locale.lowercase()]!![key]!!
        }
        else {
            loadedLanguageMap["en_us"]!![key]!!
        }
    }

    fun format(commandSender: CommandSender, key: String, noPrefix: Boolean = false) : String {
        return if(noPrefix) getString(commandSender, key) else "[§aJEXT§f] ${getString(commandSender, key)}"
    }

    fun hasLanguage(locale: String) : Boolean {
        return  loadedLanguageMap.containsKey(locale.lowercase())
    }
}

fun CommandSender.send(message: String) {
    if(CONFIG.LANGUAGE_MODE.lowercase() == "silent") {
        if(this !is Player) {
            sendMessage(message)
        }
    }
    else {
        sendMessage(message)
    }
}