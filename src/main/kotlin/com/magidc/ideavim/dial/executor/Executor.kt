package com.magidc.ideavim.dial.executor

import com.magidc.ideavim.dial.model.Match
import com.magidc.ideavim.dial.model.RegexUtils

enum class ExecutorPriority(val value: Int) {
  BASIC(1),
  LANGUAGE_SPECIFIC(2),
  CUSTOM_EXECUTOR(3),
}

open class Executor(
  val category: String,
  val group: String,
  regexPattern: String,
  val forwardTransform: (MatchResult) -> String?,
  val backwardTransform: (MatchResult) -> String?,
  val preserveCase: Boolean = false,
  val matchWithin: Boolean = false,
  val id: String = category + "_" + group,
  var priority: ExecutorPriority = ExecutorPriority.BASIC,
  private val regex: Regex = regexPattern.toRegex(),
) {
  open fun findMatch(text: String, cursorOffset: Int, reverse: Boolean): Match? {
    for (matchResult: MatchResult in regex.findAll(text)) {
      var start: Int
      var end: Int
      if (!regex.toString().contains("$")) {
        start = matchResult.range.first + matchResult.value.takeWhile { it.isWhitespace() }.length
        end = matchResult.range.last - matchResult.value.takeLastWhile { it.isWhitespace() }.length
      } else {
        start = matchResult.range.first
        end = matchResult.range.last
      }
      // Caret can be in the middle of the matched word. Match can start before the caret but cannot
      // end before it.
      // tru<caret>e -> OK
      // true<caret> -> NOT OK
      if (matchWithin && end < cursorOffset) {
        continue
      }
      if (!matchWithin && start < cursorOffset) {
        continue
      }

      var replacement =
        (if (reverse) backwardTransform(matchResult) else forwardTransform(matchResult)) ?: continue

      if (replacement.contains("$")) {
        replacement = matchResult.value.replace(regex, replacement)
      }

      if (preserveCase) {
        if (matchResult.value.all { c -> c.isUpperCase() }) {
          replacement = replacement.uppercase()
        } else if (
          matchResult.value.firstOrNull()?.isUpperCase() == true &&
            matchResult.value.substring(1).all { it.isLowerCase() }
        ) {
          replacement = replacement.replaceFirstChar { it.uppercase() }
        }
      }
      return Match(start, end, replacement, this)
    }
    return null
  }

  override fun toString(): String = id
}

fun regexExecutor(
  category: String,
  group: String,
  pattern: String,
  replacementPattern: String,
  matchWithin: Boolean = false,
): Executor =
  Executor(
    category,
    group,
    pattern,
    { _: MatchResult -> replacementPattern },
    { _: MatchResult -> replacementPattern },
    pattern.startsWith("(?i)"),
    matchWithin,
  )

private fun buildWordSetExecutors(
  category: String,
  group: String,
  vararg words: String,
  wholeWords: Boolean,
  preserveCase: Boolean = false,
  matchWithin: Boolean = true,
): Executor {
  var pattern =
    if (wholeWords) {
      "\\b(?:${words.joinToString("|") { RegexUtils.word(it) }})\\b"
    } else {
      "(?:${words.joinToString("|") { Regex.escape(it) }})"
    }

  if (preserveCase) {
    pattern = RegexUtils.caseInsensitive(pattern)
  }

  val forwardMap: Map<String, String> =
    words
      .mapIndexed { index, word ->
        val nextIndex = (index + 1) % words.size
        word.lowercase() to words[nextIndex]
      }
      .toMap()
  val backwardMap: Map<String, String> =
    words
      .mapIndexed { index, word ->
        val prevIndex = (index - 1 + words.size) % words.size
        word.lowercase() to words[prevIndex]
      }
      .toMap()
  return Executor(
    category,
    group,
    pattern,
    { matchResult: MatchResult -> forwardMap[matchResult.value.lowercase()] ?: matchResult.value },
    { matchResult: MatchResult -> backwardMap[matchResult.value.lowercase()] ?: matchResult.value },
    preserveCase,
    matchWithin,
  )
}

fun wordSet(
  category: String,
  group: String,
  vararg words: String,
  wholeWords: Boolean = true,
): Executor = buildWordSetExecutors(category, group, words = words, wholeWords = wholeWords)

fun normalizedCaseWordSet(
  category: String,
  group: String,
  vararg words: String,
  wholeWords: Boolean = true,
): Executor =
  buildWordSetExecutors(
    category,
    group,
    words = words,
    wholeWords = wholeWords,
    preserveCase = true,
  )
