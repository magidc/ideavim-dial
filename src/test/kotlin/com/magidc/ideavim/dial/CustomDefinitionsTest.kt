package com.magidc.ideavim.dial

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.magidc.ideavim.dial.executor.impl.BasicExecutors
import org.assertj.core.api.Assertions.assertThat

class CustomDefinitionsTest : BaseTest() {
    override fun getDefinitions(): String = BasicExecutors.category

    override fun getCustomDefinitions(): VimList {
        // Pattern set 1: Case insensitive with word boundaries
//        val normalizedCaseWordSet = createNormalizedCaseWordSet(listOf(VimString("one"), VimString("two"), VimString("three")))
        val normalizedCaseWordSet = VimList(
            mutableListOf(
                VimString("normalizedCaseWords"),
                VimList(mutableListOf(VimString("one"), VimString("two"), VimString("three")))
            )
        )

        // Pattern set 2: Case sensitive with word boundaries
//        val wordSet = createWordSet(listOf(VimString("un"), VimString("deux"), VimString("trois")))
        val wordSet = VimList(
            mutableListOf(
                VimString("words"),
                VimList(mutableListOf(VimString("un"), VimString("deux"), VimString("trois")))
            )
        )

        // Pattern set 3: Case insensitive without word boundaries
//        val normalizedCasePatterns = createNormalizedCasePattern(listOf(VimString("alpha"), VimString("beta"), VimString("gamma")))
        val normalizedCasePatterns = VimList(
            mutableListOf(
                VimString("normalizedCasePattern"),
                VimList(mutableListOf(VimString("alpha"), VimString("beta"), VimString("gamma")))
            )
        )

        return VimList(mutableListOf(normalizedCaseWordSet, wordSet, normalizedCasePatterns))
    }

    /** Ensure that custom definitions take precedence over built-in definitions. See #4. */
    fun testCustomDefinitionsTakePrecedence() {
        // When on true/false, dial between those words as the true/false rule
        // is defined before the quote rule in the basic executor
        assertThat(execute("\"" + CARET + "true\"")).isEqualTo("\"" + CARET + "false\"")
        // When on the quote, the quote rule is applied
        assertThat(execute("" + CARET + "\"true\"")).isEqualTo("" + CARET + "'true'")
        assertThat(execute("" + CARET + "\"one\"")).isEqualTo("" + CARET + "'one'")
        // When on one/two, dial between those words as the custom one/two rule
        // takes precedence over the quote rule from the built-in basic executor
        assertThat(execute("\"" + CARET + "one\"")).isEqualTo("\"" + CARET + "two\"")
    }

    fun testWordBoundaryPatterns() {
        // Test case sensitivity (should not dial due to case mismatch)
        assertThat(execute("" + CARET + "Un")).isEqualTo("" + CARET + "Un")

        // Test word boundaries
        assertThat(execute("" + CARET + "un")).isEqualTo("" + CARET + "deux")
        assertThat(execute("pre" + CARET + "un")).isEqualTo("pre" + CARET + "un") // Should not match within word
        assertThat(execute("" + CARET + "un.")).isEqualTo("" + CARET + "deux.")
    }

    fun testNormalizedCaseWordPatterns() {
        // Test case insensitivity and case preservation for uniform case
        assertThat(execute("" + CARET + "ONE")).isEqualTo("" + CARET + "TWO") // Preserves uppercase
        assertThat(execute("" + CARET + "one")).isEqualTo("" + CARET + "two") // Preserves lowercase
        // Mixed case defaults to lowercase
        assertThat(execute("" + CARET + "OnE")).isEqualTo("" + CARET + "two")

        // Test word boundaries
        assertThat(execute("someone")).isEqualTo("someone") // Should not match within word
        assertThat(execute("" + CARET + "one.")).isEqualTo("" + CARET + "two.")
    }

    fun testNormalizedCasePatterns() {
        assertThat(execute("meg" + CARET + "alpha")).isEqualTo("meg" + CARET + "beta")

        // Test case insensitivity and case preservation for uniform case
        assertThat(execute("" + CARET + "ALPHA")).isEqualTo("" + CARET + "BETA") // Preserves uppercase
        assertThat(execute("" + CARET + "alpha")).isEqualTo("" + CARET + "beta") // Preserves lowercase
        // Mixed case defaults to lowercase
        assertThat(execute("" + CARET + "AlPhA")).isEqualTo("" + CARET + "beta")

        // Test no word boundaries (should match within words)
        assertThat(execute("meg" + CARET + "alpha")).isEqualTo("meg" + CARET + "beta")
        assertThat(execute("" + CARET + "alpha.")).isEqualTo("" + CARET + "beta.")
    }

    fun testPatternCycling() {
        // Test cycling through multiple values
        // Normalized case word executor
        assertThat(execute("" + CARET + "one")).isEqualTo("" + CARET + "two")
        assertThat(execute("" + CARET + "two")).isEqualTo("" + CARET + "three")
        assertThat(execute("" + CARET + "three")).isEqualTo("" + CARET + "one")

        // Word boundary executor
        assertThat(execute("" + CARET + "un")).isEqualTo("" + CARET + "deux")
        assertThat(execute("" + CARET + "deux")).isEqualTo("" + CARET + "trois")
        assertThat(execute("" + CARET + "trois")).isEqualTo("" + CARET + "un")

        // Normalized case executor
        assertThat(execute("" + CARET + "alpha")).isEqualTo("" + CARET + "beta")
        assertThat(execute("" + CARET + "beta")).isEqualTo("" + CARET + "gamma")
        assertThat(execute("" + CARET + "gamma")).isEqualTo("" + CARET + "alpha")
    }
}
