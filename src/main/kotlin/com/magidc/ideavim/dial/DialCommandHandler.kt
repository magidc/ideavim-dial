package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.magidc.ideavim.dial.executor.Executor
import com.magidc.ideavim.dial.model.Match
import java.util.LinkedList
import java.util.Optional

class DialCommandHandler(
    private val reverse: Boolean,
    private val executors: LinkedList<Executor>,
    private val editorAdapter: EditorAdapter = EditorAdapter(),
) : CommandAliasHandler {
    companion object {
        private class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
                return size > maxSize
            }
        }

        // Cache for Pair<caret_position_in_trimmed_line, trimmed_line_text> to Executor that matched it
        private val executorCache = LRUCache<Pair<Int, String>, Optional<Executor>>(100)
    }

    private fun findBestMatch(text: String, caretOffset: Int): Match? {
        var bestMatch: Match? = null
        val trimmedTextLength = text.trimStart().length
        for (executor in executors) {
            // If the executor informs its minimum replacement size, we can avoid shorter texts that will never match
            if (trimmedTextLength < executor.minReplacementLength) continue
            val match = executor.findMatch(text, caretOffset, reverse) ?: continue
            if (bestMatch == null)
                bestMatch = match
            else {
                if (match.start < bestMatch.start || (bestMatch.start == match.start && (match.end - match.start) > (bestMatch.end - bestMatch.start)))
                    bestMatch = match
            }
        }
        return bestMatch
    }

    override fun execute(command: String, range: Range, editor: VimEditor, context: ExecutionContext) {
        val lineRange = editorAdapter.getLineRange(editor)
        val text = lineRange.text
        val caretOffset = lineRange.caretOffset
        val cacheKey = Pair(caretOffset - text.takeWhile { it.isWhitespace() }.length, text.trimStart())
        val cachedExecutor = executorCache.get(cacheKey)

        val bestMatch =
            if (null != cachedExecutor)
                cachedExecutor.orElse(null)?.findMatch(text, caretOffset, reverse) ?: findBestMatch(text, caretOffset)
            else
                findBestMatch(text, caretOffset)

        if (bestMatch != null) {
            editorAdapter.replace(editor, lineRange, bestMatch)
            val replacedText = text.replaceRange(bestMatch.start, bestMatch.end + 1, bestMatch.replacement)
            val executor = bestMatch.executor
            if (executor.reusable) {
                // Cyclic executors (like word sets) are cached for the current position as it is known that they will always match
                executorCache.remove(cacheKey)
                executorCache[Pair(bestMatch.start - replacedText.takeWhile { it.isWhitespace() }.length, replacedText.trimStart())] = Optional.of(executor)
            }
            // Recently used executors to the front of the evaluation list as they are most likely to be used again
            executors.remove(executor)
            executors.addFirst(executor)
        } else {
            // To avoid checking a piece of text that doesn't match anything'
            executorCache[cacheKey] = Optional.empty()
        }
    }
}
