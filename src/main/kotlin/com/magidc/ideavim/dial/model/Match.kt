package com.magidc.ideavim.dial.model

import com.magidc.ideavim.dial.executor.Executor

// Represents a match that should be replaced, with its position and replacement text
data class Match(val start: Int, val end: Int, val replacement: String, val executor: Executor)
