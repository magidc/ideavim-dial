package com.magidc.ideavim.dial.executor.impl

import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.executor.normalizedCaseWordSet
import com.magidc.ideavim.dial.executor.regexExecutor
import com.magidc.ideavim.dial.executor.wordSet
import com.magidc.ideavim.dial.model.RegexUtils.notFollowedBy
import com.magidc.ideavim.dial.model.RegexUtils.standalone
import com.magidc.ideavim.dial.model.RegexUtils.withOptionalSpaces
import com.magidc.ideavim.dial.model.RegexUtils.withRequiredSpaces

interface ExecutorProvider {
  fun buildExecutors(): List<Executor>

  val category: String
}

object BasicExecutors : ExecutorProvider {
  override val category = "basic"

  override fun buildExecutors(): List<Executor> =
    listOf(
      normalizedCaseWordSet(category, "up_down_left_right", "up", "down", "left", "right"),
      wordSet(category, "logical_ops", "&&", "||", wholeWords = false),
      wordSet(category, "numeric_comparison", ">", "<"),
      wordSet(category, "numeric_comparison", ">=", "<="),
      regexExecutor(category, "bitwise_ops", standalone("&"), "|"),
      regexExecutor(category, "bitwise_ops", standalone("\\|"), "&"),
      normalizedCaseWordSet(category, "true_false", "true", "false"),
      normalizedCaseWordSet(category, "and_or", "and", "or"),
      regexExecutor(category, "equality", withOptionalSpaces("=="), "!=", matchWithin = true),
      regexExecutor(category, "equality", withOptionalSpaces("!="), "==", matchWithin = true),
      regexExecutor(category, "is_is_not", withRequiredSpaces(notFollowedBy("is", "\\s+not")), "is not"),
      regexExecutor(category, "is_is_not", withRequiredSpaces("is\\s+not"), "is"),
      normalizedCaseWordSet(category, "http_methods", "get", "post", "put", "delete", "patch"),
      normalizedCaseWordSet(category, "log_levels", "debug", "info", "warning", "error", "critical"),
    )
}
