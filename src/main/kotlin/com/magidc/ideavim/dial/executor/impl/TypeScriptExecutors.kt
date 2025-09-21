
package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.ExecutorPriority
import com.magidc.ideavim.dial.executor.regexExecutor
import com.magidc.ideavim.dial.executor.wordSet
import com.magidc.ideavim.dial.model.RegexUtils.capture
import com.magidc.ideavim.dial.model.RegexUtils.group
import com.magidc.ideavim.dial.model.RegexUtils.optionalCapture

object TypeScriptExecutors : ExecutorProvider {
    override val category = "typescript"

    override fun buildExecutors(): List<Executor> {
        return listOf(
            // Inherit JavaScript function transformations
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

            // TypeScript-specific type annotations
            wordSet(category, "basic_types", "string", "number", "boolean", "object", "any", "unknown", "never", "void"),
            wordSet(category, "utility_types", "Partial", "Required", "Readonly", "Pick", "Omit", "Record"),

            // Access modifiers
            wordSet(category, "access_modifiers", "public", "private", "protected", "readonly"),

            // Type assertion styles
            regexExecutor(
                category,
                "type_assertion",
                capture("[^<>\\s]+") + "\\s+as\\s+" + capture("[^;,\\)\\}\\]]+"),
                "<$2>$1"
            ),
            regexExecutor(
                category,
                "type_assertion",
                "<" + capture("[^<>]+") + ">" + capture("[^;,\\)\\}\\]]+"),
                "$2 as $1"
            ),

            // Interface/Type keywords
            regexExecutor(category, "type_definition", "interface\\s+" + capture("\\w+"), "type $1 ="),
            regexExecutor(category, "type_definition", "type\\s+" + capture("\\w+") + "\\s*=", "interface $1"),

            // Generic constraints
            regexExecutor(
                category,
                "generic_constraint",
                "<" + capture("\\w+") + "\\s+extends\\s+" + capture("[^>]+") + ">",
                "<$1 super $2>"
            ),
            regexExecutor(
                category,
                "generic_constraint",
                "<" + capture("\\w+") + "\\s+super\\s+" + capture("[^>]+") + ">",
                "<$1 extends $2>"
            ),

            // Optional/Required properties
            regexExecutor(
                category,
                "property_modifier",
                capture("\\w+") + "\\?:",
                "$1!:"
            ),
            regexExecutor(
                category,
                "property_modifier",
                capture("\\w+") + "!:",
                "$1?:"
            ),

            // Import/Export styles
            regexExecutor(
                category,
                "import_export",
                "import\\s+" + capture("[^{]+") + "\\s+from",
                "import { $1 } from"
            ),
            regexExecutor(
                category,
                "import_export",
                "import\\s*\\{\\s*" + capture("[^}]+") + "\\s*\\}\\s+from",
                "import $1 from"
            ),
            regexExecutor(
                category,
                "import_export",
                "export\\s+default\\s+" + capture(".+"),
                "export { $1 }"
            ),
            regexExecutor(
                category,
                "import_export",
                "export\\s*\\{\\s*" + capture("[^}]+") + "\\s*\\}",
                "export default $1"
            ),

            // Function return type annotations
            regexExecutor(
                category,
                "function_return_type",
                "\\):\\s*" + capture("\\w+") + "\\s*\\{",
                "): Promise<$1> {"
            ),
            regexExecutor(
                category,
                "function_return_type",
                "\\):\\s*Promise<" + capture("[^>]+") + ">\\s*\\{",
                "): $1 {"
            ),

            // Variable declarations (inherit from JavaScript but with TypeScript context)
            wordSet(category, "es6_declarations", "var", "let", "const"),

            // Assertion functions
            regexExecutor(
                category,
                "assertion",
                capture("\\w+") + "\\s*!",
                "$1?"
            ),
            regexExecutor(
                category,
                "assertion",
                capture("\\w+") + "\\?",
                "$1!"
            ),

            // Array types
            regexExecutor(
                category,
                "array_type",
                capture("\\w+") + "\\[\\]",
                "Array<$1>"
            ),
            regexExecutor(
                category,
                "array_type",
                "Array<" + capture("[^>]+") + ">",
                "$1[]"
            ),

            // Conditional types keywords
            wordSet(category, "conditional_keywords", "extends", "infer", "keyof", "typeof", "instanceof"),

            // Module keywords
            wordSet(category, "module_keywords", "namespace", "module", "declare", "abstract"),

            // Strict null checks
            regexExecutor(
                category,
                "null_checks",
                capture("\\w+") + "\\s*\\|\\s*null",
                "$1 | undefined"
            ),
            regexExecutor(
                category,
                "null_checks",
                capture("\\w+") + "\\s*\\|\\s*undefined",
                "$1 | null"
            )

        ).onEach { ex -> ex.setPriority(ExecutorPriority.LANGUAGE_SPECIFIC) }
    }
}
