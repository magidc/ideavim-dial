package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.regexExecutor


object MarkdownExecutors : ExecutorProvider {
    override val category = "markdown"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            regexExecutor(category, "task_item", "^\\s*- \\[ \\]", "- [x]", matchWithin = true),
            regexExecutor(category, "task_item", "^\\s*- \\[x\\]", "- [ ]", matchWithin = true),
        )
    }
}
