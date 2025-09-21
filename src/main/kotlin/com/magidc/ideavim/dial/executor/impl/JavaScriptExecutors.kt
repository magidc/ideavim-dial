package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.regexExecutor
import com.magidc.ideavim.dial.executor.wordSet
import com.magidc.ideavim.dial.model.RegexUtils.capture
import com.magidc.ideavim.dial.model.RegexUtils.group
import com.magidc.ideavim.dial.model.RegexUtils.optionalCapture


object JavaScriptExecutors : ExecutorProvider {
    override val category = "javascript"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            regexExecutor(
                category,
                "function",
                optionalCapture("async\\s+") + "function\\s*" + capture("\\w+") + "\\s*\\(" + optionalCapture("[^()]+") + "\\)\\s*\\{",
                "const $2 = $1($3) => {",
                true
            ),
            regexExecutor(
                category,
                "function",
                group("var|let|const") + "\\s+" + capture("\\w+") + "\\s*=\\s*" + optionalCapture("async\\s+") + "function\\s*\\(",
                "$2function $1(",
                true
            ),
             regexExecutor(
                category,
                "function",
                group("var|let|const") + "\\s+" + capture("\\w+") + "\\s*=\\s*" + optionalCapture("async\\s+") + "\\(" + optionalCapture("[^()]+") + "\\)\\s*=>\\s*\\{",
                "$2function $1($3) {",
                true
            ),
            regexExecutor(category, "arrow_function", "function\\s*\\(\\)\\s*\\{", "() => {", true),
            regexExecutor(category, "arrow_function", "function\\s*\\(" + capture("[^()]+") + "\\)\\s*\\{", "($1) => {", true),
            regexExecutor(category, "arrow_function", "\\(" + optionalCapture("[^()]+") + "\\)\\s*=>\\s*\\{", "function($1) {", true),
            regexExecutor(category, "arrow_function", capture("\\w+") + "\\s*=>\\s*\\{", "function($1) {", true),
            wordSet(JavaExecutors.category, "es6_declarations", "let", "var", "const"),
        ).onEach { ex -> ex.setPriority(ExecutorPriority.LANGUAGE_SPECIFIC) }
    }
}
