package com.magidc.ideavim.dial

import com.intellij.openapi.application.ApplicationInfo
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.impl.*
import com.magidc.ideavim.dial.executor.regexExecutor
import org.reflections.Reflections


private const val CUSTOM = "CUSTOM"

class ExecutorLoader {

    private fun scanAvailableExecutorProviders(): Map<String, ExecutorProvider> {
        val reflections = Reflections(ExecutorProvider::class.java.packageName)
        val implementations = reflections.getSubTypesOf(ExecutorProvider::class.java)

        return implementations.mapNotNull { clazz ->
            try {
                val kotlinClass = clazz.kotlin
                val instance = when {
                    kotlinClass.objectInstance != null && !kotlinClass.isAbstract -> kotlinClass.objectInstance as ExecutorProvider
                    else -> null
                }
                instance?.let { it.category to it }
            } catch (e: Exception) {
                println("Failed to instantiate ${clazz.simpleName}: ${e.message}")
                null
            }
        }.toMap()
    }

    // All available pattern definitions
    private val builtinExecutorProviders: Map<String, ExecutorProvider> = scanAvailableExecutorProviders()


    fun getDefaultIDEExecutors(): List<Executor> {
        // Get the default definitions based on the IDE name
        val ideName = ApplicationInfo.getInstance().fullApplicationName.lowercase()
        val ideSpecificExecutors: ExecutorProvider? = when {
            ideName.contains("intellij") -> builtinExecutorProviders[JavaExecutors.category]
            ideName.contains("rustrover") -> builtinExecutorProviders[RustExecutors.category]
            ideName.contains("webstorm") -> builtinExecutorProviders[JavaScriptExecutors.category]
            ideName.contains("pycharm") -> builtinExecutorProviders[PythonExecutors.category]
            else -> null
        }

        val basicExecutors: ExecutorProvider? = builtinExecutorProviders[BasicExecutors.category]
        val numberExecutors: ExecutorProvider? = builtinExecutorProviders[NumberExecutors.category]
        val dateExecutors: ExecutorProvider? = builtinExecutorProviders[DateExecutors.category]
        val markdownExecutors: ExecutorProvider? = builtinExecutorProviders[MarkdownExecutors.category]

        return sequenceOf(ideSpecificExecutors, basicExecutors, numberExecutors, dateExecutors, markdownExecutors)
            .filterNotNull()
            .flatMap { it.buildExecutors().asSequence() }
            .toList()
    }

    fun getDeclaredExecutors(definitions: String): List<Executor> {
        if (definitions.isEmpty()) return emptyList()
        return definitions.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .flatMap {
                if (it.contains(":")) {
                    val tokens = it.trim().split(":")
                    val category = tokens[0]
                    val group = tokens[1]
                    builtinExecutorProviders[category]?.buildExecutors()?.asSequence()?.filter { ex -> ex.group == group } ?: emptySequence()
                } else
                    builtinExecutorProviders[it]?.buildExecutors()?.asSequence() ?: emptySequence()
            }
            .distinct()
            .toList()
    }

    fun getCustomExecutors(customDefinitions: VimList): List<Executor> {
        //TODO: Explore ways of defining custom executors
        return customDefinitions.values
            .flatMap { v -> (v as VimList).values }
            .mapNotNull { entry ->
                val pair = entry as? VimList
                val key = (pair?.get(0) as? VimString)?.toString()
                val value = (pair?.get(1) as? VimString)?.toString()
                if (key == null || value == null)
                    null
                else
                    regexExecutor(CUSTOM, CUSTOM, key, value, true)
            }
            .toList()
    }

    // Get executor enabled in .ideavimrc, or default to basic executor
    fun getEnabledExecutors(includedDefinitions: String, customDefinitions: VimList?): List<Executor> {
        val builtinExecutors: List<Executor> = if (includedDefinitions.isEmpty()) getDefaultIDEExecutors() else getDeclaredExecutors(includedDefinitions)
        val customExecutors = if (customDefinitions != null) getCustomExecutors(customDefinitions) else emptyList()

        val usedExecutorIds = HashSet<String>()
        val usedExecutorGroups = HashSet<String>()
        // If there are multiple executors with the same group in different categories, only keep the ones from the first category
        // It allows for executor replacement
        return (customExecutors + builtinExecutors).asSequence()
            .filter { executor ->
                if (usedExecutorIds.contains(executor.id))
                    return@filter true
                if (usedExecutorGroups.add(executor.group)) {
                    usedExecutorIds.add(executor.id)
                    return@filter true
                }
                return@filter false
            }
            .toList()
    }
}
