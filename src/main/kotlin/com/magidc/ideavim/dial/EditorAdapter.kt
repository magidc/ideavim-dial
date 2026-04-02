package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.getText
import com.maddyhome.idea.vim.api.injector
import com.magidc.ideavim.dial.model.LineRange
import com.magidc.ideavim.dial.model.Match

// Handles all editor-related operations
open class EditorAdapter {
    // Extract current line information from the editor
    open fun getLineRange(editor: VimEditor): LineRange {
        val caret = editor.currentCaret()
        val line = editor.currentCaret().getLine()
        val lineStart = editor.getLineStartOffset(line)
        val lineEnd = editor.getLineEndOffset(line)

        return LineRange(
            text = editor.getText(lineStart, lineEnd),
            start = lineStart,
            end = lineEnd,
            caretOffset = caret.offset - lineStart,
        )
    }

    // Replace text in the editor within a write action
    open fun replace(editor: VimEditor, lineRange: LineRange, match: Match) {
        val matchStartOffset = lineRange.start + match.start
        injector.changeGroup.replaceText(
            editor,
            editor.currentCaret(),
            matchStartOffset,
            lineRange.start + match.end + 1,
            match.replacement
        )
        editor.currentCaret().moveToOffset(matchStartOffset)
    }
}
