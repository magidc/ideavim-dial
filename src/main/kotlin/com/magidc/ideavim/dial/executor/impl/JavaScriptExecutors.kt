package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.model.RegexUtils.capture
import com.magidc.ideavim.dial.model.RegexUtils.group
import com.magidc.ideavim.dial.model.RegexUtils.optionalCapture
import com.magidc.ideavim.dial.model.RegexUtils.word
import com.magidc.ideavim.dial.executor.regexExecutor


object JavaScriptExecutors : ExecutorProvider {
    override val category = "javascript"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            regexExecutor(
                category,
                "function",
                optionalCapture("async\\s+") + "function\\s*" + capture("\\w+") + "\\s*\\(\\)\\s*\\{",
                "const $2 = $1() => {"
            ),
            regexExecutor(
                category,
                "function",
                optionalCapture("async\\s+") + "function\\s*" + capture("\\w+") + "\\s*\\(" + capture("[^()]+") + "\\)\\s*\\{",
                "const $2 = $1($3) => {"
            ),
            regexExecutor(
                category,
                "function",
                group("var|let|const") + "\\s+" + capture("\\w+") + "\\s*=\\s*" + optionalCapture("async\\s+") + "function\\s*\\(",
                "$2function $1("
            ),
            regexExecutor(category, "arrow_function", "function\\s*\\(\\)\\s*\\{", "() => {"),
            regexExecutor(category, "arrow_function", "function\\s*\\(" + capture("[^()]+") + "\\)\\s*\\{", "($1) => {"),
            regexExecutor(category, "arrow_function", "\\(" + capture("[^()]+") + "\\)\\s*=>\\s*\\{", "function($1) {"),
            regexExecutor(category, "arrow_function", capture("\\w+") + "\\s*=>\\s*\\{", "function($1) {"),
            regexExecutor(category, "es6_declarations", word("var") + "\\s+", "let "),
            regexExecutor(category, "es6_declarations", word("let") + "\\s+", "const "),
            regexExecutor(category, "es6_declarations", word("const") + "\\s+", "let ")
        ).onEach { ex -> ex.priority = ExecutorPriority.LANGUAGE_SPECIFIC }
    }
}
