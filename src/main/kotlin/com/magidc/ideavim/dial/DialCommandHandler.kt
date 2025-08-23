package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.LineRange
import com.magidc.ideavim.dial.model.Match
import java.util.Optional

class DialCommandHandler(
  private val reverse: Boolean,
  private val executors: List<Executor>,
  private val editorAdapter: EditorAdapter = EditorAdapter(),
) : CommandAliasHandler {
  companion object {
    private val executorCache = LRUCache<String, Optional<Executor>>(500)

    private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(50, 0.75f, true) {
      override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean =
        size > maxSize
    }
  }

  override fun execute(
    command: String,
    range: Range,
    editor: VimEditor,
    context: ExecutionContext,
  ) {
    val lineRange = editorAdapter.getLineRange(editor)
    val text = lineRange.text
    val textFromCaret = text.substring(lineRange.caretOffset)
    val cachedExecutor = executorCache[textFromCaret]

    val bestMatch = findBestMatch(cachedExecutor, text, lineRange)

    if (bestMatch != null) {
      editorAdapter.replace(editor, lineRange, bestMatch)
      executorCache.remove(textFromCaret)
      executorCache[bestMatch.replacement + lineRange.text.substring(bestMatch.end + 1)] =
        Optional.of(bestMatch.executor)
    } else if (null == cachedExecutor) {
      // To avoid checking a piece of text that we know nothing matches
      executorCache[textFromCaret] = Optional.empty()
    }
  }

  private fun findMatch(
    executorStream: Sequence<Executor>,
    lineRange: LineRange,
    text: String,
  ): Match? =
    executorStream
      .mapNotNull { it.findMatch(text, lineRange.caretOffset, reverse) }
      .maxWithOrNull(
        compareByDescending<Match> { it.start }
          .thenBy { it.executor.priority }
          .thenBy { it.replacement.length }
      )

  private fun findBestMatch(
    cachedExecutor: Optional<Executor>?,
    text: String,
    lineRange: LineRange,
  ): Match? =
    if (null != cachedExecutor) {
      if (cachedExecutor.isEmpty) {
        null
      } else {
        val executor = cachedExecutor.get()
        // First checking the cached executor
        var match = executor.findMatch(text, lineRange.caretOffset, reverse)
        if (match == null) {
          // If the cached executor didn't match, we can try to find a match in the same group
          match =
            findMatch(
              executors.asSequence().filter { it != executor && it.group == executor.group },
              lineRange,
              text,
            )
          // If the cached executor didn't match and the group didn't match, trying with the rest of
          // the executors
          if (match == null) {
            match =
              findMatch(
                executors.asSequence().filter { it.group != executor.group },
                lineRange,
                text,
              )
          }
        }
        match
      }
    } else {
      findMatch(executors.asSequence(), lineRange, text)
    }
}
