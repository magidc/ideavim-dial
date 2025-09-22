package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.wordSet

object TypeScriptExecutors : ExecutorProvider {
    override val category = "typescript"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            wordSet(category, "basic_types", "string", "number", "boolean", "object", "any", "unknown", "never", "void"),
            wordSet(category, "utility_types", "Partial", "Required", "Readonly", "Pick", "Omit", "Record"),
            wordSet(category, "access_modifiers", "public", "private", "protected", "readonly"),
        ).onEach { ex ->
            ex.priority = ExecutorPriority.LANGUAGE_SPECIFIC
        }
    }
}
