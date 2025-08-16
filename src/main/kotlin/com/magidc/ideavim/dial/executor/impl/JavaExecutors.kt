package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.wordSet

object JavaExecutors : ExecutorProvider {
  override val category = "java"

  override fun buildExecutors(): List<Executor> =
    listOf(
        wordSet(category, "comparison", "==", "!=", wholeWords = false),
        wordSet(category, "assertions", "assertEquals", "assertNotEquals"),
        wordSet(category, "assertions", "assertTrue", "assertFalse"),
        wordSet(category, "assertions", "assertNull", "assertNotNull"),
        wordSet(category, "visibility", "private", "protected", "public"),
        wordSet(category, "content", ".isPresent", ".isEmpty", wholeWords = false),
        wordSet(category, "collections", "ArrayList", "HashSet"),
        wordSet(category, "basic_types", "int", "long", "float", "double", "boolean"),
        wordSet(category, "flow_control", "if", "else if"),
        wordSet(category, "loop_type", "for", "while"),
        wordSet(category, "loop_control", "break", "continue"),
        wordSet(category, "collection_operations", ".add", ".remove", wholeWords = false),
        wordSet(category, "string_case", ".toLowerCase", ".toUpperCase", wholeWords = false),
        wordSet(category, "streams", ".map", ".flatMap", wholeWords = false),
        wordSet(category, "streams", ".filter", ".peek", wholeWords = false),
        wordSet(category, "streams", ".findAny", ".findFirst", wholeWords = false),
        wordSet(category, "streams", ".anyMatch", ".allMatch", ".noneMatch", wholeWords = false),
      )
      .onEach { ex -> ex.priority = ExecutorPriority.LANGUAGE_SPECIFIC }
}
