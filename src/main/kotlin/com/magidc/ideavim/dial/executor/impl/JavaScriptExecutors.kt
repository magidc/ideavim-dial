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
        /**
         * Name Function: function name() {}
         * Named Function Expression: const x = function name() {}
         * Anonymous Function: function() {}
         * Anonymous Function Expression: const x = function() {}
         * Arrow Function: const () => {}
         * Arrow Function expression: const x = () => {}
         */

        return listOf(
            //  () => {} -----> function() {}
            regexExecutor(
                category,
                "arrow_function_to_anonymous_function",
                optionalCapture("async\\s+") + "\\(" + optionalCapture("[^()]+") + "\\)\\s*=>\\s*\\{",
                "$1function($2) {",
                matchWithin = true,
                matchBefore = false,
            ),
            //  item => {} -----> function(item) {}
            regexExecutor(
                category,
                "single_item_arrow_function_to_anonymous_function",
                optionalCapture("async\\s+") + capture("\\w+") + "\\s*=>\\s*\\{",
                "$1function($2) {",
                matchWithin = true,
                matchBefore = false,
            ),
            //  function() {} -----> () => {}
            regexExecutor(
                category,
                "anonymous_function_to_arrow_function",
                optionalCapture("async\\s+") + "function\\s*\\(" + optionalCapture("[^()]+") + "\\)\\s*\\{",
                "$1($2) => {",
                matchWithin = true,
                matchBefore = false,
            ),
            // var name = () => {} -----> function name() {}
            regexExecutor(
                category,
                "arrow_function_expression_to_named_function",
                group("var|let|const") + "\\s+" + capture("\\w+") + "\\s*=\\s*" + optionalCapture("async\\s+") + "\\(" + optionalCapture("[^()]+") + "\\)\\s*=>\\s*\\{",
                "$2function $1($3) {",
                matchWithin = true,
                matchBefore = false,
            ),
            // function name() {} -----> const name = () => {}
            regexExecutor(
                category,
                "named_function_to_arrow_function_expression",
                optionalCapture("async\\s+") + "function\\s+" + capture("\\w+") + "\\s*\\(" + optionalCapture("[^()]+") + "\\)\\s*\\{",
                "const $2 = $1($3) => {",
                matchWithin = true,
                matchBefore = false,
            ),
            // var name = function() -----> function name() {}
            regexExecutor(
                category,
                "anonymous_function_expression_to_named_function",
                group("var|let|const") + "\\s+" + capture("\\w+") + "\\s*=\\s*" + optionalCapture("async\\s+") + "function\\s*\\(" + optionalCapture("[^()]+") + "\\)\\s*\\{",
                "$2function $1($3) {",
                matchWithin = true,
                matchBefore = false,
            ),
            wordSet(category, "es6_declarations", "let", "var", "const"),
        ).onEach { ex ->
            ex.priority = ExecutorPriority.LANGUAGE_SPECIFIC
        }
    }
}
