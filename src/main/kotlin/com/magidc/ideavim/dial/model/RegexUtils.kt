package com.magidc.ideavim.dial.model

object RegexUtils {

    fun word(text: String) = "\\b$text\\b"

    fun caseInsensitive(pattern: String) = "(?i)$pattern"

    fun wordCaseInsensitive(text: String) = caseInsensitive(word(text))

    fun withOptionalSpaces(text: String) = "\\s*$text\\s*"

    fun withRequiredSpaces(text: String) = "\\s+$text\\s+"

    fun notFollowedBy(text: String, notText: String) = "$text(?!$notText)"

    fun notPrecededBy(text: String, notText: String) = "(?<!$notText)$text"

    fun standalone(text: String) = notPrecededBy(notFollowedBy(text, text), text)

    fun atLineStart(pattern: String) = "^\\s*$pattern"

    fun capture(pattern: String) = "($pattern)"

    fun optionalCapture(pattern: String) = "($pattern)?"

    fun group(pattern: String) = "(?:$pattern)"
}
