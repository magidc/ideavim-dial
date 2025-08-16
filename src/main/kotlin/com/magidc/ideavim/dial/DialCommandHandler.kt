package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.Match
import java.util.Optional

class DialCommandHandler(
  private val reverse: Boolean,
  private val executors: List<Executor>,
  private val editorAdapter: EditorAdapter = EditorAdapter(),
) : CommandAliasHandler {
  private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > maxSize
  }

  private val executorCache = LRUCache<String, Optional<Executor>>(1000)

  override fun execute(
    command: String,
    range: Range,
    editor: VimEditor,
    context: ExecutionContext,
  ) {
    val lineRange = editorAdapter.getLineRange(editor)
    val text = lineRange.text
    val textFromCaret = text.substring(lineRange.caretOffset)
    val cachedExecutor = executorCache.get(textFromCaret)

    val bestMatch =
      if (null != cachedExecutor) {
        cachedExecutor.orElse(null)?.findMatch(text, lineRange.caretOffset, reverse)
      } else {
        executors
          .parallelStream()
          .map { it.findMatch(text, lineRange.caretOffset, reverse) }
          .filter { it != null }
          .max(
            Comparator.comparingInt<Match> { -1 * (it.start) }
              .thenComparing { it.executor.priority }
              .thenComparing { it.replacement.length }
          )
          .orElse(null)
      }
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
}
