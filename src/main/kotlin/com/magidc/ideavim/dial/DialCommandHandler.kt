package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.Match
import java.util.*

class DialCommandHandler(
    private val reverse: Boolean,
    private val executors: List<Executor>,
    private val editorAdapter: EditorAdapter = EditorAdapter(),
) : CommandAliasHandler {

    private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > maxSize
        }
    }

    private val executorCache = LRUCache<Pair<Int, String>, Optional<Executor>>(100)

    override fun execute(command: String, range: Range, editor: VimEditor, context: ExecutionContext) {
        val lineRange = editorAdapter.getLineRange(editor)
        val text = lineRange.text
        val caretOffset = lineRange.caretOffset
        val cacheKey = Pair(caretOffset - text.takeWhile { it.isWhitespace() }.length, text.trimStart())
        val cachedExecutor = executorCache.get(cacheKey)

        val bestMatch =
            if (null != cachedExecutor)
                cachedExecutor.orElse(null)?.findMatch(text, caretOffset, reverse)
            else {
                executors
                    .parallelStream()
                    .map { it.findMatch(text, caretOffset, reverse) }
                    .filter { it != null }
                    .max(Comparator.comparingInt<Match> { -1 * (it.start) }.thenComparing { it.executor.priority }.thenComparing { it.replacement.length })
                    .orElse(null)
            }
        if (bestMatch != null) {
            editorAdapter.replace(editor, lineRange, bestMatch)
            executorCache.remove(cacheKey)
            val replacedText = (text.take(bestMatch.start) + bestMatch.replacement + text.substring(bestMatch.end + 1))
            executorCache[Pair(bestMatch.start - replacedText.takeWhile { it.isWhitespace() }.length, replacedText.trimStart())] = Optional.of(bestMatch.executor)
        } else if (null == cachedExecutor) {
            // To avoid checking a piece of text that we know nothing matches
            executorCache[cacheKey] = Optional.empty()
        }
    }
}
