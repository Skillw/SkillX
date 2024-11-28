package net.minestom.dependencies

import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.net.URL

/**
 * Resolved Dependency.
 * Holds its coordinates (group, artifact, version), which are allowed to be empty if needed
 *
 * The contentsLocation URL represents the location of the dependency, on local storage.
 */
data class ResolvedDependency(
    val group: String, val name: String, val version: String,
    val contentsLocation: URL, val subdependencies: List<ResolvedDependency>
) {
    fun printTree(indent: String = "") {
        logger.info("$indent- $group:$name:$version ($contentsLocation)")
        subdependencies.forEach { it.printTree("$indent  ") }
    }

    companion object {
        val logger = ComponentLogger.logger(this::class.java)
    }

}
