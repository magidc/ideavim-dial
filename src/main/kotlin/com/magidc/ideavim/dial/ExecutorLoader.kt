package com.magidc.ideavim.dial

import com.intellij.openapi.application.ApplicationInfo
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.impl.BasicExecutors
import com.magidc.ideavim.dial.executor.impl.DateExecutors
import com.magidc.ideavim.dial.executor.impl.ExecutorProvider
import com.magidc.ideavim.dial.executor.impl.JavaExecutors
import com.magidc.ideavim.dial.executor.impl.JavaScriptExecutors
import com.magidc.ideavim.dial.executor.impl.MarkdownExecutors
import com.magidc.ideavim.dial.executor.impl.NumberExecutors
import com.magidc.ideavim.dial.executor.impl.PythonExecutors
import com.magidc.ideavim.dial.executor.impl.RustExecutors
import com.magidc.ideavim.dial.executor.normalizedCaseWordSet
import com.magidc.ideavim.dial.executor.wordSet
import org.reflections.Reflections

private const val CUSTOM = "CUSTOM"

class ExecutorLoader {
  private fun scanAvailableExecutorProviders(): Map<String, ExecutorProvider> {
    val reflections = Reflections(ExecutorProvider::class.java.packageName)
    val implementations = reflections.getSubTypesOf(ExecutorProvider::class.java)

    return implementations
      .mapNotNull { clazz ->
        try {
          val kotlinClass = clazz.kotlin
          val instance =
            when {
              kotlinClass.objectInstance != null && !kotlinClass.isAbstract ->
                kotlinClass.objectInstance as ExecutorProvider
              else -> null
            }
          instance?.let { it.category to it }
        } catch (e: Exception) {
          println("Failed to instantiate ${clazz.simpleName}: ${e.message}")
          null
        }
      }
      .toMap()
  }

  // All available pattern definitions
  private val builtinExecutorProviders: Map<String, ExecutorProvider> =
    scanAvailableExecutorProviders()

  fun getDefaultIDEExecutors(): List<Executor> {
    // Get the default definitions based on the IDE name
    val ideName = ApplicationInfo.getInstance().fullApplicationName.lowercase()
    val ideSpecificExecutors: ExecutorProvider? =
      when {
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

    return sequenceOf(
        ideSpecificExecutors,
        basicExecutors,
        numberExecutors,
        dateExecutors,
        markdownExecutors,
      )
      .filterNotNull()
      .flatMap { it.buildExecutors().asSequence() }
      .toList()
  }

  fun getDeclaredExecutors(definitions: String): List<Executor> {
    if (definitions.isEmpty()) return emptyList()
    return definitions
      .split(",")
      .map { it.trim() }
      .filter { it.isNotEmpty() }
      .flatMap {
        if (it.contains(":")) {
          val tokens = it.trim().split(":")
          val category = tokens[0]
          val group = tokens[1]
          builtinExecutorProviders[category]?.buildExecutors()?.asSequence()?.filter { ex ->
            ex.group == group
          } ?: emptySequence()
        } else {
          builtinExecutorProviders[it]?.buildExecutors()?.asSequence() ?: emptySequence()
        }
      }
      .distinct()
      .toList()
  }

  fun getCustomExecutors(customDefinitions: VimList): List<Executor> {
    return customDefinitions.values
      .mapNotNull { rule ->
        val pair = rule as? VimList
        val functionName = (pair?.get(0) as? VimString)?.toString()
        val wordList =
          (pair?.get(1) as? VimList)?.values?.map { it.asString() }?.toList()?.toTypedArray()
        if (functionName == null || wordList == null || wordList.isEmpty()) return@mapNotNull null
        when (functionName) {
          Dial.WORDSET_FUNCTION ->
            return@mapNotNull wordSet(CUSTOM, CUSTOM, words = wordList, wholeWords = true)

          Dial.NORMALIZED_CASE_WORDSET_FUNCTION ->
            return@mapNotNull normalizedCaseWordSet(
              CUSTOM,
              CUSTOM,
              words = wordList,
              wholeWords = true,
            )

          Dial.PATTERN_FUNCTION ->
            return@mapNotNull wordSet(CUSTOM, CUSTOM, words = wordList, wholeWords = false)

          Dial.NORMALIZED_CASE_PATTERN_FUNCTION ->
            return@mapNotNull normalizedCaseWordSet(
              CUSTOM,
              CUSTOM,
              words = wordList,
              wholeWords = false,
            )
        }
        return@mapNotNull null
      }
      // Custom executors have the highest priority
      .onEach { ex -> ex.priority = ExecutorPriority.CUSTOM_EXECUTOR }
      .toList()
  }

  // Get executor enabled in .ideavimrc, or default to basic executor
  fun getEnabledExecutors(
    includedDefinitions: String,
    customDefinitions: VimList?,
  ): List<Executor> {
    val builtinExecutors: List<Executor> =
      if (includedDefinitions.isEmpty()) {
        getDefaultIDEExecutors()
      } else {
        getDeclaredExecutors(includedDefinitions)
      }
    val customExecutors =
      if (customDefinitions != null) getCustomExecutors(customDefinitions) else emptyList()

    val usedExecutorIds = HashSet<String>()
    val usedExecutorGroups = HashSet<String>()
    // If there are multiple executors with the same group in different categories, only keep the
    // ones from the first category
    // It allows for executor replacement
    return (customExecutors + builtinExecutors)
      .asSequence()
      .filter { executor ->
        if (usedExecutorIds.contains(executor.id)) return@filter true
        if (usedExecutorGroups.add(executor.group)) {
          usedExecutorIds.add(executor.id)
          return@filter true
        }
        return@filter false
      }
      .toList()
  }
}
