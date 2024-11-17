package com.skillw.skillx

import com.skillw.skillx.plugin.PluginManager
import com.skillw.skillx.terminal.EasyTerminal
import net.minestom.server.MinecraftServer

object SkillwX {

    fun start() {
        val server = MinecraftServer.init()
        EasyTerminal.start()
        PluginManager.loadPlugins()

        server.start("0.0.0.0", 25565)

        PluginManager.activePlugins()
        MinecraftServer.getSchedulerManager().buildShutdownTask {
            close()
        }
    }

    fun close() {
        MinecraftServer.getSchedulerManager().buildShutdownTask {
            PluginManager.disablePlugins()
        }
    }


}

fun main() {
    SkillwX.start()
}