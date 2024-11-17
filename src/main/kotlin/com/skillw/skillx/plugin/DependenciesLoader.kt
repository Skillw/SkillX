package com.skillw.skillx.plugin

import com.skillw.skillx.plugin.simple.PluginClassLoader
import net.minestom.dependencies.ResolvedDependency

interface DependenciesLoader {

    fun loadDependencies(description: PluginDescription, classLoader: PluginClassLoader)
    fun loadDependicyToPlugin(description: PluginDescription, dependency: ResolvedDependency, classLoader: PluginClassLoader)


}