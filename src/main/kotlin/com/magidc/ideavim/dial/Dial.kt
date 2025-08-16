package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade
import com.maddyhome.idea.vim.helper.noneOfEnum
import com.maddyhome.idea.vim.vimscript.model.ExecutionResult
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimDataType
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.magidc.ideavim.dial.executor.Executor
import org.jetbrains.annotations.NonNls

// Extension for switching between common text executor (e.g. true/false, &&/||, etc.)
// Also provides VimScript functions for custom pattern definitions
class Dial : VimExtension {
    companion object {
        @NonNls
        const val DIAL_INCLUDED_DEFINITIONS_VARIABLE_NAME = "dial_include"

        @NonNls
        const val DIAL_CUSTOM_DEFINITIONS_VARIABLE_NAME = "dial_custom"

    }

    override fun getName(): String = "dial"

    override fun init() {
        // Export VimScript functions for custom pattern definitions
        registerWordSetFunction()
        registerNormalizedCaseWordSetFunction()
        registerPatternFunction()
        registerNormalizedCasePatternFunction()

        // Register main dial commands
        val enabledReplacementExecutors = getEnabledExecutors()

        VimExtensionFacade.addCommand(
            "DialIncrement",
            0,
            0,
            DialCommandHandler(false, enabledReplacementExecutors),
        )
        VimExtensionFacade.addCommand(
            "DialDecrement",
            0,
            0,
            DialCommandHandler(true, enabledReplacementExecutors),
        )
    }


    private fun registerWordSetFunction() {
        VimExtensionFacade.exportScriptFunction(
            scope = null,
            name = "dialWords",
            args = listOf("words"),
            defaultArgs = emptyList(),
            hasOptionalArguments = false,
            flags = noneOfEnum(),
        ) { _, _, args ->
            val words = args["words"]
            if (words !is VimList) {
                return@exportScriptFunction ExecutionResult.Error
            }
            val patterns = createWordSet(words.values)
            ExecutionResult.Return(patterns)
        }
    }

    private fun registerNormalizedCaseWordSetFunction() {
        VimExtensionFacade.exportScriptFunction(
            scope = null,
            name = "dialNormalizedCaseWords",
            args = listOf("words"),
            defaultArgs = emptyList(),
            hasOptionalArguments = false,
            flags = noneOfEnum(),
        ) { _, _, args ->
            val words = args["words"]
            if (words !is VimList) {
                return@exportScriptFunction ExecutionResult.Error
            }
            val patterns = createNormalizedCaseWordSet(words.values)
            ExecutionResult.Return(patterns)
        }
    }

    private fun registerPatternFunction() {
        VimExtensionFacade.exportScriptFunction(
            scope = null,
            name = "dialPattern",
            args = listOf("words"),
            defaultArgs = emptyList(),
            hasOptionalArguments = false,
            flags = noneOfEnum(),
        ) { _, _, args ->
            val words = args["words"]
            if (words !is VimList) {
                return@exportScriptFunction ExecutionResult.Error
            }
            val patterns = createPattern(words.values)
            ExecutionResult.Return(patterns)
        }
    }

    private fun registerNormalizedCasePatternFunction() {
        VimExtensionFacade.exportScriptFunction(
            scope = null,
            name = "dialNormalizedCasePattern",
            args = listOf("words"),
            defaultArgs = emptyList(),
            hasOptionalArguments = false,
            flags = noneOfEnum(),
        ) { _, _, args ->
            val words = args["words"]
            if (words !is VimList) {
                return@exportScriptFunction ExecutionResult.Error
            }
            val patterns = createNormalizedCasePattern(words.values)
            ExecutionResult.Return(patterns)
        }
    }
}

private val executorLoader = ExecutorLoader()

// Get executor enabled via dial_definitions in .ideavimrc
private fun getEnabledExecutors(): List<Executor> {
    val builtinDefinitions = VimPlugin.getVariableService().getGlobalVariableValue(Dial.DIAL_INCLUDED_DEFINITIONS_VARIABLE_NAME)?.toString() ?: ""
    val customDefinitions = VimPlugin.getVariableService().getGlobalVariableValue(Dial.DIAL_CUSTOM_DEFINITIONS_VARIABLE_NAME) as? VimList ?: VimList(mutableListOf())
    return executorLoader.getEnabledExecutors(builtinDefinitions, customDefinitions)
}

private fun interface PatternFormat {
    fun format(word: String): String
}

private fun buildPattern(words: List<VimDataType>, formatPattern: PatternFormat): VimList {
    val list = ArrayList<VimDataType>().apply {
        words.forEachIndexed { index, word ->
            val nextIndex = (index + 1) % words.size
            add(
                VimList(
                    mutableListOf(
                        VimString(formatPattern.format(word.toString())),
                        VimString(words[nextIndex].toString()),
                    ),
                ),
            )
        }
    }
    return VimList(list)
}


fun createNormalizedCaseWordSet(words: List<VimDataType>): VimList =
    buildPattern(words) { word -> "(?i)\\b$word\\b" }

fun createWordSet(words: List<VimDataType>): VimList =
    buildPattern(words) { word -> "\\b$word\\b" }

fun createNormalizedCasePattern(words: List<VimDataType>): VimList =
    buildPattern(words) { word -> "(?i)$word" }

fun createPattern(words: List<VimDataType>): VimList =
    buildPattern(words) { word -> word }
