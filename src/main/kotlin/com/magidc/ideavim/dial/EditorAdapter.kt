package com.magidc.ideavim.dial

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.newapi.ij
import com.magidc.ideavim.dial.model.LineRange
import com.magidc.ideavim.dial.model.Match

// Handles all editor-related operations
open class EditorAdapter {
    // Extract current line information from the editor
    open fun getLineRange(editor: VimEditor): LineRange {
        val ijEditor = editor.ij
        val document = ijEditor.document
        val caret = ijEditor.caretModel.primaryCaret
        val line = document.getLineNumber(caret.offset)
        val lineStart = document.getLineStartOffset(line)
        val lineEnd = document.getLineEndOffset(line)

        return LineRange(
            text = document.getText(TextRange(lineStart, lineEnd)),
            start = lineStart,
            end = lineEnd,
            caretOffset = caret.offset - lineStart,
        )
    }

    // Replace text in the editor within a write action
    open fun replace(editor: VimEditor, lineRange: LineRange, match: Match) {
        WriteCommandAction.runWriteCommandAction(editor.ij.project) {
            val matchStartOffset = lineRange.start + match.start
            editor.ij.document.replaceString(
                matchStartOffset,
                lineRange.start + match.end + 1,
                match.replacement,
            )
            editor.ij.caretModel.moveToOffset(matchStartOffset)
        }
    }
}
