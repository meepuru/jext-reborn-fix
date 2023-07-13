package me.spartacus04.jext.config

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import me.spartacus04.jext.jukebox.JukeboxContainer
import me.spartacus04.jext.jukebox.legacy.LegacyJukeboxContainer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Type

class ConfigManager {
    companion object {
        private lateinit var configFile: File
        private lateinit var discsFile: File

        /**
         * The function creates a default configuration file for a Java plugin if it doesn't already exist.
         *
         * @param plugin The parameter "plugin" is of type JavaPlugin. It is used to reference the plugin instance that is
         * calling the createDefaultConfig function.
         */
        private fun createDefaultConfig(plugin: JavaPlugin) {
            if(!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()

            if(!configFile.exists()) {
                configFile.createNewFile()

                plugin.getResource("config.json")!!.bufferedReader().use {
                    configFile.writeText(it.readText())
                }
            }
        }

        /* The `deserialize` function is a generic function that takes a `File` object and a `Type` object as parameters
        and returns an object of type `T`. */
        private fun <T> deserialize(file: File, type: Type) : T {
            val gson = GsonBuilder().setLenient().setPrettyPrinting().create()

            return gson.fromJson(
                file.readText(),
                type
            )
        }

        /**
         * The function loads configuration and disc data from files, updates the configuration if necessary, and then
         * loads the data into the appropriate variables.
         *
         * @param plugin The "plugin" parameter is an instance of the JavaPlugin class. It is used to access the plugin's
         * data folder and to send messages to the console.
         */
        fun load(plugin: JavaPlugin) {
            configFile = plugin.dataFolder.resolve("config.json")
            discsFile = plugin.dataFolder.resolve("discs.json")

            if(!configFile.exists()) createDefaultConfig(plugin)
            if(!discsFile.exists()) {
                Bukkit.getConsoleSender().sendMessage(LanguageManager.DISCS_NOT_FOUND)

                throw FileNotFoundException("discs.json file not found!")
            }

            val configType = object : TypeToken<Config>() {}.type
            val discsType = object : TypeToken<List<Disc>>() {}.type

            ConfigVersionManager.updateConfig(configFile, plugin)
            ConfigData.CONFIG = deserialize(configFile, configType)

            ConfigVersionManager.updateDiscs(discsFile)
            ConfigData.DISCS = deserialize(discsFile, discsType)

            LegacyJukeboxContainer.reload(plugin)
            JukeboxContainer.loadFromFile()
        }
    }
}