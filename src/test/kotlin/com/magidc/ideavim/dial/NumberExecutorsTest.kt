package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.NumberExecutors
import org.assertj.core.api.Assertions.assertThat

class NumberExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = NumberExecutors.category

    fun testVersionCodes() {
        assertThat(execute("version$CARET = 10.23.0-SNAPSHOT")).isEqualTo("version = ${CARET}10.23.1-SNAPSHOT")
        assertThat(execute("version$CARET = 10.23.9-SNAPSHOT")).isEqualTo("version = ${CARET}10.23.10-SNAPSHOT")

        assertThat(execute("version$CARET = 10.23.4-SNAPSHOT")).isEqualTo("version = ${CARET}10.23.5-SNAPSHOT")
        assertThat(execute("10$CARET.23.4.RC1")).isEqualTo("${CARET}10.23.5.RC1")
        assertThat(execute("${CARET}10.23.4")).isEqualTo("${CARET}10.23.5")

        assertThat(execute("version$CARET = 10.23.4-SNAPSHOT", true)).isEqualTo("version = ${CARET}10.23.3-SNAPSHOT")
        assertThat(execute("10$CARET.23.4.RC1", true)).isEqualTo("${CARET}10.23.3.RC1")
        assertThat(execute("${CARET}10.23.4", true)).isEqualTo("${CARET}10.23.3")
        assertThat(execute("${CARET}10.23.0", true)).isEqualTo("${CARET}10.22.9")
        assertThat(execute("${CARET}10.23.00", true)).isEqualTo("${CARET}10.22.99")
    }

    fun testPositiveInteger() {
        assertThat(execute(CARET + "0")).isEqualTo(CARET + "1")
        assertThat(execute(CARET + "1")).isEqualTo(CARET + "2")
        assertThat(execute("$CARET 10")).isEqualTo(" " + CARET + "11")
        assertThat(execute(CARET + "0", true)).isEqualTo("$CARET-1")
        assertThat(execute(CARET + "1", true)).isEqualTo(CARET + "0")
        assertThat(execute("$CARET 10", true)).isEqualTo(" " + CARET + "9")
    }

    fun testNegativeIntegers() {
        assertThat(execute("$CARET-1")).isEqualTo(CARET + "0")
        assertThat(execute("$CARET -10")).isEqualTo(" " + CARET + "-9")
        assertThat(execute("$CARET-1", true)).isEqualTo("$CARET-2")
        assertThat(execute("$CARET -10", true)).isEqualTo(" " + CARET + "-11")
    }

    fun testPositiveDecimals() {
        assertThat(execute("$CARET 10.0")).isEqualTo(" " + CARET + "10.1")
        assertThat(execute("$CARET 10.123")).isEqualTo(" " + CARET + "10.124")
        assertThat(execute(CARET + "1.0")).isEqualTo(CARET + "1.1")
        assertThat(execute("$CARET 10.0", true)).isEqualTo(" " + CARET + "9.9")
        assertThat(execute("$CARET 1.0", true)).isEqualTo(" " + CARET + "0.9")
    }

    fun testNegativeDecimals() {
        assertThat(execute("$CARET -10.0")).isEqualTo(" " + CARET + "-9.9")
        assertThat(execute("$CARET -10.123")).isEqualTo(" " + CARET + "-10.122")
        assertThat(execute("$CARET-1.0")).isEqualTo("$CARET-0.9")
        assertThat(execute("$CARET -10.0", true)).isEqualTo(" " + CARET + "-10.1")
        assertThat(execute("$CARET -1.0", true)).isEqualTo(" " + CARET + "-1.1")
    }

    fun testPositiveScientific() {
        assertThat(execute("$CARET 1e2")).isEqualTo(" " + CARET + "2e2")
        assertThat(execute("$CARET 1E2")).isEqualTo(" " + CARET + "2E2")
        assertThat(execute("$CARET 1.0e-2")).isEqualTo(" " + CARET + "1.1e-2")
        assertThat(execute("$CARET 1.0E-2")).isEqualTo(" " + CARET + "1.1E-2")
        assertThat(execute("$CARET 1e2", true)).isEqualTo(" " + CARET + "0e2")
        assertThat(execute("$CARET 1E2", true)).isEqualTo(" " + CARET + "0E2")
        assertThat(execute("$CARET 1.0e-2", true)).isEqualTo(" " + CARET + "0.9e-2")
        assertThat(execute("$CARET 1.0E-2", true)).isEqualTo(" " + CARET + "0.9E-2")
    }

    fun testNegativeScientific() {
        assertThat(execute("$CARET -1.0e-2")).isEqualTo(" " + CARET + "-0.9e-2")
        assertThat(execute("$CARET -1e2")).isEqualTo(" " + CARET + "0e2")
        assertThat(execute("$CARET -1E2")).isEqualTo(" " + CARET + "0E2")
        assertThat(execute("$CARET -1.0E-2")).isEqualTo(" " + CARET + "-0.9E-2")
        assertThat(execute("$CARET -1e2", true)).isEqualTo(" " + CARET + "-2e2")
        assertThat(execute("$CARET -1E2", true)).isEqualTo(" " + CARET + "-2E2")
        assertThat(execute("$CARET -1.0e-2", true)).isEqualTo(" " + CARET + "-1.1e-2")
        assertThat(execute("$CARET -1.0E-2", true)).isEqualTo(" " + CARET + "-1.1E-2")
    }
}
