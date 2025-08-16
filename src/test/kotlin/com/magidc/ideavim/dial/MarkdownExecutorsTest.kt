package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.MarkdownExecutors
import org.assertj.core.api.Assertions.assertThat

class MarkdownExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = MarkdownExecutors.category

    fun testBasicTaskToggle() {
        assertThat(execute("- [$CARET ] Task")).isEqualTo("$CARET- [x] Task")
        assertThat(execute("- [" + CARET + "x] Task")).isEqualTo("$CARET- [ ] Task")
    }

    fun testTasksWithIndentation() {
        assertThat(execute("  - [$CARET ] Indented task")).isEqualTo("  $CARET- [x] Indented task")
        assertThat(execute("    - [" + CARET + "x] Double indented")).isEqualTo("    $CARET- [ ] Double indented")
    }

    fun testTasksWithDifferentCaretPositions() {
        // On the checkbox
        assertThat(execute("- [ $CARET] Task")).isEqualTo("$CARET- [x] Task")
        assertThat(execute("- [x$CARET] Task")).isEqualTo("$CARET- [ ] Task")
        // On the dash
        assertThat(execute("-$CARET [ ] Task")).isEqualTo("$CARET- [x] Task")
        // On the task text
        assertThat(execute("- [ ] Task$CARET")).isEqualTo("- [ ] Task$CARET")
    }

    fun testTasksWithVariousContent() {
        assertThat(execute("- [$CARET ] Task with * special ^ characters")).isEqualTo("$CARET- [x] Task with * special ^ characters")
        assertThat(execute("- [" + CARET + "x] Task with [link](url)")).isEqualTo("$CARET- [ ] Task with [link](url)")
        assertThat(execute("- [$CARET ] Task with **bold** and *italic*")).isEqualTo("$CARET- [x] Task with **bold** and *italic*")
    }

    fun testNoMatchForInvalidSyntax() {
        // Missing space after dash
        val invalid1 = "-[$CARET ] Invalid"
        assertThat(execute(invalid1)).isEqualTo(invalid1)

        // Wrong checkbox syntax
        val invalid2 = "- ($CARET ) Wrong brackets"
        assertThat(execute(invalid2)).isEqualTo(invalid2)

        // Not at start of line
        val invalid3 = "Some text - [$CARET ] Invalid"
        assertThat(execute(invalid3)).isEqualTo(invalid3)
    }
}
