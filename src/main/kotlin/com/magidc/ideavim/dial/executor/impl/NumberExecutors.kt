package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

object NumberExecutors : ExecutorProvider {
  override val category = "numbers"

  val VERSION_CODE_DELIMITERS = setOf(".", "-", "_")
  val VERSION_CODE_REGEX = "(?=[_.-])|(?<=[_.-])".toRegex()

  private fun incrementVersionCode(matchResult: MatchResult, increment: Boolean = true): String? {
    val tokens =
      matchResult.value
        .split(VERSION_CODE_REGEX)
        .filter { it.isNotEmpty() }
        .reversed()
        .toMutableList()
    if (tokens.size < 5) return null
    for (i in tokens.indices) {
      try {
        val token = tokens[i]
        if (VERSION_CODE_DELIMITERS.contains(token)) continue
        Integer.valueOf(token)
        val newVersion = incrementWithPrecision(token, increment) ?: return null
        if (Integer.valueOf(newVersion) < 0) {
          tokens[i] = "99"
          continue
        }
        tokens[i] = newVersion
        return tokens.reversed().joinToString("")
      } catch (e: Throwable) {}
    }
    return null
  }

  private fun incrementWithPrecision(value: String?, increment: Boolean = true): String? {
    if (value.isNullOrEmpty()) return "0"
    val decimalIndex = value.indexOf('.')
    return if (decimalIndex != -1) {
      val decimalPlaces = value.length - decimalIndex - 1
      val precisionStep = BigDecimal.valueOf(10.0.pow(-decimalPlaces))
      val currentValue = BigDecimal(value)
      val newValue =
        if (increment) currentValue.add(precisionStep) else currentValue.subtract(precisionStep)
      newValue.setScale(decimalPlaces, RoundingMode.HALF_UP).toPlainString()
    } else if (value.contains('e') || value.contains('E')) {
      // Handle scientific notation
      val e = if (value.contains("e")) "e" else "E"
      val parts = value.split(e)
      if (parts.size != 2) return null // Invalid scientific notation
      val base = BigDecimal(parts[0])
      val exponent = parts[1].toInt()
      val newBase = if (increment) base.add(BigDecimal.ONE) else base.subtract(BigDecimal.ONE)
      "${newBase.toPlainString()}$e$exponent"
    } else {
      return (value.toInt() + (if (increment) 1 else -1)).toString()
    }
  }

  override fun buildExecutors(): List<Executor> =
    listOf(
      Executor(
        category,
        "version_codes",
        "\\d+(?:\\.\\d+)*(?:[\\.\\-_]\\w+)*",
        { matchResult: MatchResult -> incrementVersionCode(matchResult) },
        { matchResult: MatchResult -> incrementVersionCode(matchResult, false) },
        matchWithin = true,
      ),
      Executor(
        category,
        "signed_decimals",
        "[+-]\\d+[.,]\\d+",
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value) },
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value, false) },
        matchWithin = true,
      ),
      Executor(
        category,
        "unsigned_decimals",
        "(?<![+-])\\d+[.,]\\d+",
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value) },
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value, false) },
        matchWithin = true,
      ),
      Executor(
        category,
        "signed_integers",
        "[+-]\\d+(?![.,]\\d)",
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value) },
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value, false) },
        matchWithin = true,
      ),
      Executor(
        category,
        "unsigned_integers",
        "(?<![+-])\\d+(?![.,]\\d)",
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value) },
        { matchResult: MatchResult -> incrementWithPrecision(matchResult.groups[0]?.value, false) },
        matchWithin = true,
      ),
    )
}
