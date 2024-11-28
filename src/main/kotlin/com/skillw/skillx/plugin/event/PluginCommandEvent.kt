package com.skillw.skillx.plugin.event

import com.skillw.skillx.plugin.Plugin
import net.minestom.server.command.builder.Command
import net.minestom.server.event.Event

class PluginCommandEvent {
    class Registry(val plugin: Plugin, val command: Command) : PluginEvent
    class UnRegister(val plugin: Plugin, val command: Command) : PluginEvent
}