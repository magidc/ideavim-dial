package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.RegexUtils.atLineStart
import com.magidc.ideavim.dial.model.RegexUtils.capture
import com.magidc.ideavim.dial.executor.regexExecutor
import com.magidc.ideavim.dial.executor.wordSet


object RustExecutors : ExecutorProvider {
    override val category = "rust"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            regexExecutor(category, "void_typecheck", capture("let\\s*(?:mut\\s*)?[\\w]+") + "\\s*=", "$1: () ="),
            regexExecutor(category, "void_typecheck", capture("let\\s*(?:mut\\s*)?[\\w]+") + ":\\s*\\(\\)\\s*=", "$1 ="),
            regexExecutor(category, "turbofish", capture("[\\w]+") + "\\(", "$1::<Todo>("),
            regexExecutor(category, "turbofish", capture("[\\w]+") + "::<[\\w\\s<>,]+>\\(", "$1("),
            regexExecutor(category, "string", "\"" + capture("[^\"]+") + "\"", "r\"$1\""),
            regexExecutor(category, "string", "r\"" + capture("[^\"]+") + "\"", "r#\"$1\"#"),
            regexExecutor(category, "string", "r#\"" + capture("[^\"]+") + "\"#", "\"$1\""),
            wordSet(category, "is_some", "is_some", "is_none"),
            wordSet(category, "assert", "assert_eq!", "assert_ne!"),
            regexExecutor(
                category,
                "cargo_dependency_version",
                atLineStart(capture("[\\w-]+") + "\\s*=\\s*" + capture("[\"'].+[\"']")),
                "$1 = { version = $2 }"
            ),
            regexExecutor(
                category,
                "cargo_dependency_version",
                atLineStart(capture("[\\w-]+") + "\\s*=\\s*\\{\\s*version\\s*=\\s*" + capture("[\"'].+[\"']") + "\\s*\\}"),
                "$1 = $2"
            )
        )
    }
}
