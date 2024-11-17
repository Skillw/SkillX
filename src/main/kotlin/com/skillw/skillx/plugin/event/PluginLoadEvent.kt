package com.skillw.skillx.plugin.event

import com.skillw.skillx.plugin.Plugin
import com.skillw.skillx.plugin.PluginLoadState

class PluginLoadEvent(val plugin: Plugin?, state: PluginLoadState) : PluginEvent