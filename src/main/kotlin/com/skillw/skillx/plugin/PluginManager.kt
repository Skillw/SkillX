package com.skillw.skillx.plugin

import com.skillw.skillx.plugin.event.PluginActiveEvent
import com.skillw.skillx.plugin.event.PluginDisableEvent
import com.skillw.skillx.plugin.event.PluginEnableEvent
import com.skillw.skillx.plugin.event.PluginLoadEvent
import com.skillw.skillx.plugin.simple.JvmPluginLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventDispatcher
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

object PluginManager {

    internal val libsFile = File("libs")
    internal val pluginFile = File("plugins")

    val LOGGER = LoggerFactory.getLogger(PluginManager::class.java)
    var plugins = LinkedHashMap<String, Plugin>()
        private set
    val classLoaders = LinkedList<PluginLoader>()


    init {
        // JVMPluginLoader注册操作
        classLoaders.addFirst(JvmPluginLoader)

    }

//    enum class State(val value: Int) {
//        DO_NOT_START(-1), // 不启动
//        NOT_STARTED(0),  // 未启动
//        PRE_INIT(1),     // 初始化前
//        INIT(2),         // 初始化
//        POST_INIT(3),     // 初始化后
//        STARTED(4),      // 已启动
//    }

    //加载插件
    fun loadPlugin(file: File): Plugin? {
        classLoaders.forEach {
            if (it.isFileIgnored(file)) {
                try {
                    val plugin = it.loadPlugin(file)
                    plugins[plugin!!.origin.name] = plugin
                    EventDispatcher.call(PluginLoadEvent(plugin, PluginLoadState.SUCCESS))
                    return plugin
                } catch (e: Exception) {
                    e.printStackTrace()
                    EventDispatcher.call(PluginLoadEvent(null, PluginLoadState.ERROR))
                    return null
                }
            }
        }
        return null
    }

    fun loadPlugins() {
//        state = State.PRE_INIT
        if (!pluginFile.exists()) {
            if (!pluginFile.mkdirs()) {
                LOGGER.error("无法找到或创建插件文件夹，插件将不会被加载！")
                return
            }
        }

        if (!libsFile.exists()) {
            if (!libsFile.mkdirs()) {
                LOGGER.error("无法找到或创建插件依赖文件夹，插件将不会被加载！")
                return
            }
        }

        pluginFile.listFiles()?.forEach {
            loadPlugin(it)
        }

        plugins = topologicalSort(plugins)

        enablePlugins()

    }

    fun enablePlugin(plugin: Plugin) {
        if (plugin.enable) {
            LOGGER.error("插件${plugin.origin.name}已经被启用了！")
        }
        plugin.pluginLoader.openPlugin(plugin)
        EventDispatcher.call(PluginEnableEvent(plugin))
    }

    //开启插件
    fun enablePlugins() {
        plugins.forEach { (_, u) ->
            enablePlugin(u)
        }
    }

    //拓扑排序
    private fun topologicalSort(plugins: LinkedHashMap<String, Plugin>): LinkedHashMap<String, Plugin> {
        val inDegree = mutableMapOf<String, Int>() // 存储每个节点的入度
        val adjacencyList = mutableMapOf<String, MutableList<String>>() // 存储每个节点的邻居列表

        // 初始化图
        for (pluginName in plugins.keys) {
            inDegree[pluginName] = 0
            adjacencyList[pluginName] = mutableListOf()
        }

        // 构建图并检查缺失依赖项
        val pluginsToRemove = mutableSetOf<String>()
        for ((pluginName, plugin) in plugins) {
            val description = plugin.origin

            // 获取所有依赖项（包括软依赖和加载前）
            val allDependencies = description.depend + description.softDepend + description.loadBefore
            for (dependency in allDependencies) {
                if (!plugins.containsKey(dependency)) {
                    if (description.depend.contains(dependency)) {
                        LOGGER.error("Error: $pluginName is missing dependency: $dependency")
                        pluginsToRemove.add(pluginName) // 标记需要移除的插件
                    }
                } else {
                    adjacencyList[dependency]!!.add(pluginName) // 添加边
                    inDegree[pluginName] = inDegree[pluginName]!! + 1 // 增加入度
                }
            }
        }

        // 移除有缺失依赖项的插件
        for (pluginName in pluginsToRemove) {
            plugins.remove(pluginName)
        }

        // 找出所有入度为0的节点
        val zeroInDegreeQueue = ArrayDeque<String>()
        for ((node, degree) in inDegree) {
            if (degree == 0) {
                zeroInDegreeQueue.add(node)
            }
        }

        // 执行拓扑排序
        val sortedOrder = mutableListOf<String>()
        while (zeroInDegreeQueue.isNotEmpty()) {
            val node = zeroInDegreeQueue.removeFirst()
            sortedOrder.add(node)

            // 减少邻居节点的入度
            for (neighbor in adjacencyList[node]!!) {
                inDegree[neighbor] = inDegree[neighbor]!! - 1
                if (inDegree[neighbor] == 0) {
                    zeroInDegreeQueue.add(neighbor)
                }
            }
        }

        // 检查图中是否有循环
        if (sortedOrder.size != plugins.size) {
            throw IllegalStateException("Depend ERROR")
        }

        // 构建排序后的 LinkedHashMap
        val sortedPlugins = LinkedHashMap<String, Plugin>()
        for (pluginName in sortedOrder) {
            sortedPlugins[pluginName] = plugins[pluginName]!!
        }

        return sortedPlugins
    }

    //激活插件
    fun activePlugin(plugin: Plugin) {
        if (plugin.active) {
            LOGGER.error("插件${plugin.origin.name}已经被激活了！")
        }
        plugin.pluginLoader.disablePlugin(plugin)
        EventDispatcher.call(PluginActiveEvent(plugin))
    }

    fun activePlugins() {
        plugins.forEach { (_, plugin) ->
            activePlugin(plugin)
        }
    }

    fun disablePlugins() {
        plugins.forEach { (_, plugin) ->
            disablePlugin(plugin)
        }
    }

    //关闭插件
    fun disablePlugin(plugin: Plugin) {
        plugin.pluginLoader.disablePlugin(plugin)
        EventDispatcher.call(PluginDisableEvent(plugin))
    }


}