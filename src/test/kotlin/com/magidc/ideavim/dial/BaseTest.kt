package com.magidc.ideavim.dial

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.ex.ranges.Range
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.maddyhome.idea.vim.vimscript.services.VariableService
import com.magidc.ideavim.dial.model.LineRange
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

abstract class BaseTest : BasePlatformTestCase() {
    companion object {
        const val CARET = "<caret>"
    }

    @Mock
    private lateinit var variableService: VariableService

    private lateinit var vimPluginMock: AutoCloseable

    private val executorLoader = ExecutorLoader()

    override fun setUp() {
        super.setUp()
        MockitoAnnotations.openMocks(this)

        vimPluginMock =
            mockStatic(VimPlugin::class.java).apply {
                `when`<VariableService> { VimPlugin.getVariableService() }.thenReturn(variableService)
            }

        `when`(variableService.getGlobalVariableValue(Dial.DIAL_INCLUDED_DEFINITIONS_VARIABLE_NAME))
            .thenReturn(VimString(getDefinitions()))

        `when`(variableService.getGlobalVariableValue(Dial.DIAL_CUSTOM_DEFINITIONS_VARIABLE_NAME))
            .thenReturn(getCustomDefinitions())
    }

    abstract fun getDefinitions(): String

    open fun getCustomDefinitions(): VimList? {
        return null
    }

    override fun tearDown() {
        vimPluginMock.close()
        super.tearDown()
    }

    protected fun execute(input: String, reverse: Boolean = false): String {
        val caretIndex = input.indexOf(CARET)
        if (caretIndex == -1) return input

        val text = input.replace(CARET, "")
        val mockEditorAdapter = MockEditorAdapter(LineRange(text, 0, text.length, caretIndex))
        val executors = executorLoader.getEnabledExecutors(getDefinitions(), getCustomDefinitions())

        DialCommandHandler(reverse, executors, mockEditorAdapter).execute("", Range(), MockVimEditor(), MockExecutionContext())

        // No match found, return original input
        val match = mockEditorAdapter.match ?: return input
        return text.take(match.start) + CARET + match.replacement + text.substring(match.end + 1)
    }
}
