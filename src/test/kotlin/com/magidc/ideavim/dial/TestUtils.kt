package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.api.*
import com.maddyhome.idea.vim.common.LiveRange
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.common.VimEditorReplaceMask
import com.maddyhome.idea.vim.state.mode.Mode
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.magidc.ideavim.dial.model.LineRange
import com.magidc.ideavim.dial.model.Match

class MockEditorAdapter(val lineRange: LineRange) : EditorAdapter() {
    var match: Match? = null
    override fun getLineRange(editor: VimEditor): LineRange {
        return lineRange
    }

    override fun replace(editor: VimEditor, lineRange: LineRange, match: Match) {
        this.match = match
    }
}

class MockExecutionContext : ExecutionContext {
    override val context: Any
        get() = TODO("Mock")
}

class MockVimEditor(override var replaceMask: VimEditorReplaceMask? = null) : VimEditor {
    override var mode: Mode
        get() = TODO("Mock")
        set(value) {}
    override var isReplaceCharacter: Boolean
        get() = TODO("Mock")
        set(value) {}
    override val lfMakesNewLine: Boolean
        get() = TODO("Mock")
    override var vimChangeActionSwitchMode: Mode?
        get() = TODO("Mock")
        set(value) {}
    override val indentConfig: VimIndentConfig
        get() = TODO("Mock")
    override val projectId: String
        get() = TODO("Mock")
    override var vimLastSelectionType: SelectionType?
        get() = TODO("Mock")
        set(value) {}
    override var insertMode: Boolean
        get() = TODO("Mock")
        set(value) {}
    override val document: VimDocument
        get() = TODO("Mock")

    override fun fileSize(): Long {
        TODO("Mock")
    }

    override fun text(): CharSequence {
        TODO("Mock")
    }

    override fun nativeLineCount(): Int {
        TODO("Mock")
    }

    override fun getLineRange(line: Int): Pair<Int, Int> {
        TODO("Mock")
    }

    override fun carets(): List<VimCaret> {
        TODO("Mock")
    }

    override fun nativeCarets(): List<VimCaret> {
        TODO("Mock")
    }

    override fun forEachCaret(action: (VimCaret) -> Unit) {
        TODO("Mock")
    }

    override fun forEachNativeCaret(action: (VimCaret) -> Unit, reverse: Boolean) {
        TODO("Mock")
    }

    override fun isInForEachCaretScope(): Boolean {
        TODO("Mock")
    }

    override fun primaryCaret(): VimCaret {
        TODO("Mock")
    }

    override fun currentCaret(): VimCaret {
        TODO("Mock")
    }

    override fun isWritable(): Boolean {
        TODO("Mock")
    }

    override fun isDocumentWritable(): Boolean {
        TODO("Mock")
    }

    override fun isOneLineMode(): Boolean {
        TODO("Mock")
    }

    override fun search(
        pair: Pair<Int, Int>,
        editor: VimEditor,
        shiftType: LineDeleteShift
    ): Pair<Pair<Int, Int>, LineDeleteShift>? {
        TODO("Mock")
    }

    override fun offsetToBufferPosition(offset: Int): BufferPosition {
        TODO("Mock")
    }

    override fun bufferPositionToOffset(position: BufferPosition): Int {
        TODO("Mock")
    }

    override fun offsetToVisualPosition(offset: Int): VimVisualPosition {
        TODO("Mock")
    }

    override fun visualPositionToOffset(position: VimVisualPosition): Int {
        TODO("Mock")
    }

    override fun visualPositionToBufferPosition(position: VimVisualPosition): BufferPosition {
        TODO("Mock")
    }

    override fun bufferPositionToVisualPosition(position: BufferPosition): VimVisualPosition {
        TODO("Mock")
    }

    override fun getVirtualFile(): VimVirtualFile {
        TODO("Mock")
    }

    override fun deleteString(range: TextRange) {
        TODO("Mock")
    }

    override fun getSelectionModel(): VimSelectionModel {
        TODO("Mock")
    }

    override fun getScrollingModel(): VimScrollingModel {
        TODO("Mock")
    }

    override fun removeCaret(caret: VimCaret) {
        TODO("Mock")
    }

    override fun removeSecondaryCarets() {
        TODO("Mock")
    }

    override fun vimSetSystemBlockSelectionSilently(start: BufferPosition, end: BufferPosition) {
        TODO("Mock")
    }

    override fun getLineStartOffset(line: Int): Int {
        TODO("Mock")
    }

    override fun getLineEndOffset(line: Int): Int {
        TODO("Mock")
    }

    override fun addCaretListener(listener: VimCaretListener) {
        TODO("Mock")
    }

    override fun removeCaretListener(listener: VimCaretListener) {
        TODO("Mock")
    }

    override fun isDisposed(): Boolean {
        TODO("Mock")
    }

    override fun removeSelection() {
        TODO("Mock")
    }

    override fun getPath(): String? {
        TODO("Mock")
    }

    override fun extractProtocol(): String? {
        TODO("Mock")
    }

    override fun exitInsertMode(context: ExecutionContext) {
        TODO("Mock")
    }

    override fun exitSelectModeNative(adjustCaret: Boolean) {
        TODO("Mock")
    }

    override fun isTemplateActive(): Boolean {
        TODO("Mock")
    }

    override fun startGuardedBlockChecking() {
        TODO("Mock")
    }

    override fun stopGuardedBlockChecking() {
        TODO("Mock")
    }

    override fun hasUnsavedChanges(): Boolean {
        TODO("Mock")
    }

    override fun getLastVisualLineColumnNumber(line: Int): Int {
        TODO("Mock")
    }

    override fun createLiveMarker(start: Int, end: Int): LiveRange {
        TODO("Mock")
    }

    override fun createIndentBySize(size: Int): String {
        TODO("Mock")
    }

    override fun getFoldRegionAtOffset(offset: Int): VimFoldRegion? {
        TODO("Mock")
    }

    override fun <T : ImmutableVimCaret> findLastVersionOfCaret(caret: T): T? {
        TODO("Mock")
    }
}