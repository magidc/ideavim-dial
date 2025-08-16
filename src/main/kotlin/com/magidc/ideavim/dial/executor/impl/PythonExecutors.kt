package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.regexExecutor
import com.magidc.ideavim.dial.executor.wordSet

object PythonExecutors : ExecutorProvider {
  override val category = "python"

  override fun buildExecutors(): List<Executor> =
    listOf(
        wordSet(category, "comparison", "==", "!="),
        wordSet(category, "comparison", "is not", "is"),
        wordSet(category, "comparison", "not in", "in"),
        wordSet(category, "basic_types", "int", "float", "str", "bool"),
        wordSet(category, "flow_control", "if", "elif"),
        wordSet(category, "loop_type", "for", "while"),
        wordSet(category, "loop_control", "break", "continue"),
        wordSet(category, "methods", "def", "async def"),
        wordSet(category, "async", "with", "async with"),
        wordSet(
          category,
          "class_decorators",
          "@property",
          "@classmethod",
          "@staticmethod",
          wholeWords = false,
        ),
        wordSet(
          category,
          "collection_types",
          "list(",
          "tuple(",
          "set(",
          "dict(",
          wholeWords = false,
        ),
        wordSet(
          category,
          "collection_operations",
          ".append(",
          ".extend(",
          ".insert(",
          ".remove(",
          ".pop(",
          wholeWords = false,
        ),
        wordSet(category, "string_case", ".upper()", ".lower()", wholeWords = false),
        wordSet(category, "assertions", "assertTrue", "assertFalse", wholeWords = false),
        wordSet(category, "assertions", "assertEqual", "assertNotEqual", wholeWords = false),
        wordSet(category, "assertions", "assertIn", "assertNotIn", wholeWords = false),
        regexExecutor(category, "quotes", "\"([^\"]+)\"", "'$1'"),
        regexExecutor(category, "quotes", "'([^']+)'", "\"$1\""),
      )
      .onEach { ex -> ex.priority = ExecutorPriority.LANGUAGE_SPECIFIC }
}
