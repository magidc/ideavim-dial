package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.BasicExecutors
import org.assertj.core.api.Assertions.assertThat

class BasicExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = BasicExecutors.category

    fun testUpDownLeftRight() {
        assertThat(execute("value = '" + CARET + "up'")).isEqualTo("value = '" + CARET + "down'")
        assertThat(execute("value = '" + CARET + "down'")).isEqualTo("value = '" + CARET + "left'")
        assertThat(execute("value = '" + CARET + "left'")).isEqualTo("value = '" + CARET + "right'")
        assertThat(execute("value = '" + CARET + "right'")).isEqualTo("value = '" + CARET + "up'")
    }

    fun testLogicalOps() {
        assertThat(execute("if (x " + CARET + "&& y)")).isEqualTo("if (x " + CARET + "|| y)")
        assertThat(execute("if (x " + CARET + "|| y)")).isEqualTo("if (x " + CARET + "&& y)")

        // Test with different caret positions
        assertThat(execute("if (x &" + CARET + "& y)")).isEqualTo("if (x " + CARET + "|| y)")
        assertThat(execute("if (x |" + CARET + "| y)")).isEqualTo("if (x " + CARET + "&& y)")
    }

    fun testBitwiseOps() {
        assertThat(execute("if (x " + CARET + "& y)")).isEqualTo("if (x " + CARET + "| y)")
        assertThat(execute("if (x " + CARET + "| y)")).isEqualTo("if (x " + CARET + "& y)")

        // Test with different caret positions
        assertThat(execute("if (x " + CARET + "& y)")).isEqualTo("if (x " + CARET + "| y)")
        assertThat(execute("if (x " + CARET + "| y)")).isEqualTo("if (x " + CARET + "& y)")
    }

    fun testCapitalTrueFalse() {
        assertThat(execute("if (" + CARET + "True)")).isEqualTo("if (" + CARET + "False)")
        assertThat(execute("if (" + CARET + "False)")).isEqualTo("if (" + CARET + "True)")

        // Test with different caret positions
        assertThat(execute("if (Tr" + CARET + "ue)")).isEqualTo("if (" + CARET + "False)")
        assertThat(execute("if (Fal" + CARET + "se)")).isEqualTo("if (" + CARET + "True)")
    }

    fun testLowercaseTrueFalse() {
        assertThat(execute("if (" + CARET + "true)")).isEqualTo("if (" + CARET + "false)")
        assertThat(execute("if (" + CARET + "false)")).isEqualTo("if (" + CARET + "true)")

        // Test with different caret positions
        assertThat(execute("if (tr" + CARET + "ue)")).isEqualTo("if (" + CARET + "false)")
        assertThat(execute("if (fal" + CARET + "se)")).isEqualTo("if (" + CARET + "true)")
    }

    fun testNoMatchForPartialWords() {
        // Should not match parts of larger words
        assertThat(execute("if (something" + CARET + "true)")).isEqualTo("if (something" + CARET + "true)")
        assertThat(execute("if (truly" + CARET + "False)")).isEqualTo("if (truly" + CARET + "False)")
    }

    fun testMultipleOccurrencesChoosesClosestToCursor() {
        assertThat(execute("if (true && " + CARET + "true)")).isEqualTo("if (true && " + CARET + "false)")
        assertThat(execute("if (" + CARET + "true && false)")).isEqualTo("if (" + CARET + "false && false)")
    }

    fun testWithSurroundingSpacesAndPunctuation() {
        assertThat(execute("value = " + CARET + "true;")).isEqualTo("value = " + CARET + "false;")
        assertThat(execute("return " + CARET + "True,")).isEqualTo("return " + CARET + "False,")
    }

    fun testAndOr() {
        assertThat(execute("if (x " + CARET + "and y)")).isEqualTo("if (x " + CARET + "or y)")
        assertThat(execute("if (x " + CARET + "or y)")).isEqualTo("if (x " + CARET + "and y)")

        // Test with different caret positions
        assertThat(execute("if (x a" + CARET + "nd y)")).isEqualTo("if (x " + CARET + "or y)")
        assertThat(execute("if (x o" + CARET + "r y)")).isEqualTo("if (x " + CARET + "and y)")
    }

    fun testUppercaseAndOr() {
        assertThat(execute("if (x " + CARET + "AND y)")).isEqualTo("if (x " + CARET + "OR y)")
        assertThat(execute("if (x " + CARET + "OR y)")).isEqualTo("if (x " + CARET + "AND y)")

        // Test with different caret positions
        assertThat(execute("if (x A" + CARET + "ND y)")).isEqualTo("if (x " + CARET + "OR y)")
        assertThat(execute("if (x O" + CARET + "R y)")).isEqualTo("if (x " + CARET + "AND y)")
    }

    fun testAndOrWithWordBoundaries() {
        // Should not match parts of larger words
        assertThat(execute("if (expand" + CARET + "or)")).isEqualTo("if (expand" + CARET + "or)")
        assertThat(execute("if (command" + CARET + "and)")).isEqualTo("if (command" + CARET + "and)")
    }

    fun testAndOrWithSurroundingPunctuation() {
        assertThat(execute("return x " + CARET + "AND y,")).isEqualTo("return x " + CARET + "OR y,")
        assertThat(execute("(x " + CARET + "and y);")).isEqualTo("(x " + CARET + "or y);")
    }

    fun testEquality() {
        assertThat(execute("if x " + CARET + "== y")).isEqualTo("if x " + CARET + "!= y")
        assertThat(execute("if x " + CARET + "!= y")).isEqualTo("if x " + CARET + "== y")

        // Test with different spacing
        assertThat(execute("if x" + CARET + "==y")).isEqualTo("if x" + CARET + "!=y")
        assertThat(execute("if x  " + CARET + "==  y")).isEqualTo("if x  " + CARET + "!=  y")
    }

    fun testInNotIn() {
        assertThat(execute("if x " + CARET + "in y")).isEqualTo("if x " + CARET + "not in y")
        assertThat(execute("if x " + CARET + "not in y")).isEqualTo("if x " + CARET + "in y")

        // Test with different spacing
        assertThat(execute("if x" + CARET + "in y")).isEqualTo("if x" + CARET + "in y")
        assertThat(execute("if x  " + CARET + "not  in  y")).isEqualTo("if x  " + CARET + "in  y")
        
        // Caret is within the words
        assertThat(execute("if x i" + CARET + "n y")).isEqualTo("if x " + CARET + "not in y")
        assertThat(execute("if x not " + CARET + "in y")).isEqualTo("if x " + CARET + "in y")
        assertThat(execute("if x no" + CARET + "t in y")).isEqualTo("if x " + CARET + "in y")
    }

    fun testIsIsNot() {
        assertThat(execute("if x " + CARET + "is y")).isEqualTo("if x " + CARET + "is not y")
        assertThat(execute("if x " + CARET + "is not y")).isEqualTo("if x " + CARET + "is y")

        // Test with different spacing
        assertThat(execute("if x" + CARET + "is y")).isEqualTo("if x" + CARET + "is y")
        assertThat(execute("if x  " + CARET + "is  not  y")).isEqualTo("if x  " + CARET + "is  y")

         // Caret is within the words
        assertThat(execute("if x i" + CARET + "s y")).isEqualTo("if x " + CARET + "is not y")
        assertThat(execute("if x is " + CARET + "not y")).isEqualTo("if x " + CARET + "is y")
        assertThat(execute("if x i" + CARET + "s not y")).isEqualTo("if x " + CARET + "is y")
    }

    fun testHTTPMethods() {
        assertThat(execute("response = requests." + CARET + "get('url')")).isEqualTo("response = requests." + CARET + "post('url')")
        assertThat(execute("response = requests." + CARET + "post('url')")).isEqualTo("response = requests." + CARET + "put('url')")
        assertThat(execute("response = requests." + CARET + "GET('url')")).isEqualTo("response = requests." + CARET + "POST('url')")
        assertThat(execute("response = requests." + CARET + "PUT('url')")).isEqualTo("response = requests." + CARET + "DELETE('url')")

        // Test with different caret positions
        assertThat(execute("response = requests.g" + CARET + "et('url')")).isEqualTo("response = requests." + CARET + "post('url')")
        assertThat(execute("response = requests.p" + CARET + "ost('url')")).isEqualTo("response = requests." + CARET + "put('url')")
    }

    fun testLogLevels() {
        assertThat(execute("logger." + CARET + "debug('message')")).isEqualTo("logger." + CARET + "info('message')")
        assertThat(execute("logger." + CARET + "info('message')")).isEqualTo("logger." + CARET + "warning('message')")
        assertThat(execute("logger." + CARET + "WARNING('message')")).isEqualTo("logger." + CARET + "ERROR('message')")
        assertThat(execute("logger." + CARET + "error('message')")).isEqualTo("logger." + CARET + "critical('message')")
        assertThat(execute("logger." + CARET + "CRITICAL('message')")).isEqualTo("logger." + CARET + "DEBUG('message')")

        // Test with different caret positions
        assertThat(execute("logger.d" + CARET + "ebug('message')")).isEqualTo("logger." + CARET + "info('message')")
        assertThat(execute("logger.i" + CARET + "nfo('message')")).isEqualTo("logger." + CARET + "warning('message')")
    }
}
