package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.LineRange
import com.magidc.ideavim.dial.model.Match
import java.util.*
import java.util.stream.Stream

class DialCommandHandler(
    private val reverse: Boolean,
    private val executors: List<Executor>,
    private val editorAdapter: EditorAdapter = EditorAdapter(),
) : CommandAliasHandler {
    private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > maxSize
    }

    private val executorCache = LRUCache<String, Optional<Executor>>(1000)


    override fun execute(command: String, range: Range, editor: VimEditor, context: ExecutionContext) {
        val lineRange = editorAdapter.getLineRange(editor)
        val text = lineRange.text
        val textFromCaret = text.substring(lineRange.caretOffset)
        val cachedExecutor = executorCache[textFromCaret]

        val bestMatch = findBestMatch(cachedExecutor, text, lineRange)

        if (bestMatch != null) {
            editorAdapter.replace(editor, lineRange, bestMatch)
            executorCache.remove(textFromCaret)
            executorCache[bestMatch.replacement + lineRange.text.substring(bestMatch.end + 1)] = Optional.of(bestMatch.executor)
        } else if (null == cachedExecutor) {
            // To avoid checking a piece of text that we know nothing matches
            executorCache[textFromCaret] = Optional.empty()
        }
    }

    private fun findMatch(executorStream: Stream<Executor>, lineRange: LineRange, text: String): Match? {
        return executorStream
            .map { it.findMatch(text, lineRange.caretOffset, reverse) }
            .filter { it != null }
            .max(
                Comparator.comparingInt<Match> { -1 * (it.start) }
                    .thenComparing { it.executor.priority }
                    .thenComparing { it.replacement.length }
            )
            .orElse(null)
    }

    private fun findBestMatch(cachedExecutor: Optional<Executor>?, text: String, lineRange: LineRange): Match? =
        if (null != cachedExecutor) {
            if (cachedExecutor.isEmpty) null
            else {
                val executor = cachedExecutor.get()
                // First checking the cached executor
                var match = executor.findMatch(text, lineRange.caretOffset, reverse)
                if (match == null)
                    // If the cached executor didn't match, we can try to find a match in the same group
                    match = findMatch(executors.parallelStream().filter { it != executor && it.group == executor.group }, lineRange, text)
                if (match == null)
                    // If the cached executor didn't match and the group didn't match, trying with the rest of the executors
                    match = findMatch(executors.parallelStream().filter { it.group != executor.group }, lineRange, text)
                match
            }
        } else
            findMatch(executors.parallelStream(), lineRange, text)
}
