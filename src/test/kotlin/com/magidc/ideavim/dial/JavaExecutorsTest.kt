package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.BasicExecutors
import com.magidc.ideavim.dial.executor.impl.JavaExecutors
import org.assertj.core.api.Assertions.assertThat

class JavaExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = JavaExecutors.category + "," + BasicExecutors.category

    fun testJavaAssertions() {
        // Test assertTrue / assertFalse
        assertThat(execute(CARET + "assertTrue(condition)")).isEqualTo(CARET + "assertFalse(condition)")
        assertThat(execute(CARET + "assertFalse(result)")).isEqualTo(CARET + "assertTrue(result)")

        // Test different caret positions
        assertThat(execute("assert" + CARET + "True(x)")).isEqualTo(CARET + "assertFalse(x)")
        assertThat(execute("assertT" + CARET + "rue(condition)"))
            .isEqualTo(CARET + "assertFalse(condition)")
        assertThat(execute("assertTrue(condition$CARET)")).isEqualTo("assertTrue(condition$CARET)")
    }

    fun testJavaOptionalChecks() {
        // Test isPresent / isEmpty
        assertThat(execute(CARET + "optional.isPresent()")).isEqualTo("optional$CARET.isEmpty()")
        assertThat(execute(CARET + "optional.isEmpty()")).isEqualTo("optional$CARET.isPresent()")

        // Test different caret positions
        assertThat(execute("optional.is" + CARET + "Present()")).isEqualTo("optional$CARET.isEmpty()")
        assertThat(execute("optional.isEmpty($CARET)")).isEqualTo("optional.isEmpty($CARET)")
    }

    fun testJavaVisibilityModifiers() {
        // Test public / private / protected
        assertThat(execute(CARET + "public class Test")).isEqualTo(CARET + "private class Test")
        assertThat(execute(CARET + "private class Test")).isEqualTo(CARET + "protected class Test")
        assertThat(execute(CARET + "protected class Test")).isEqualTo(CARET + "public class Test")

        // Test different caret positions
        assertThat(execute("pub" + CARET + "lic void method()"))
            .isEqualTo(CARET + "private void method()")
        assertThat(execute("private void method($CARET)")).isEqualTo("private void method($CARET)")
    }

    fun testStringCase() {
        assertThat(execute(CARET + "string.toUpperCase()")).isEqualTo("string$CARET.toLowerCase()")
        assertThat(execute(CARET + "string.toLowerCase()")).isEqualTo("string$CARET.toUpperCase()")

        // Test different caret positions
        assertThat(execute("string.toUpper" + CARET + "Case()")).isEqualTo("string$CARET.toLowerCase()")
        assertThat(execute("string.toLowerCase($CARET)")).isEqualTo("string.toLowerCase($CARET)")
    }

    fun testComparisonOperators() {
        assertThat(execute("if (a $CARET== b)")).isEqualTo("if (a $CARET!= b)")
        assertThat(execute("if (a $CARET!= b)")).isEqualTo("if (a $CARET== b)")

        // Test different caret positions within operators
        assertThat(execute("if (a =$CARET= b)")).isEqualTo("if (a $CARET!= b)")
        assertThat(execute("if (a !$CARET= b)")).isEqualTo("if (a $CARET== b)")
    }

    fun testAssertions() {
        assertThat(execute(CARET + "assertEquals(expected, actual)"))
            .isEqualTo(CARET + "assertNotEquals(expected, actual)")
        assertThat(execute(CARET + "assertNotEquals(a, b)")).isEqualTo(CARET + "assertEquals(a, b)")

        // Test different caret positions
        assertThat(execute("assert" + CARET + "Equals(a, b)"))
            .isEqualTo(CARET + "assertNotEquals(a, b)")
        assertThat(execute("assertEquals(a, b$CARET)")).isEqualTo("assertEquals(a, b$CARET)")
    }

    fun testVisibilityModifiers() {
        assertThat(execute(CARET + "public static void")).isEqualTo(CARET + "private static void")
        assertThat(execute(CARET + "private final int")).isEqualTo(CARET + "protected final int")
        assertThat(execute(CARET + "protected String")).isEqualTo(CARET + "public String")

        // Test different caret positions
        assertThat(execute("pub" + CARET + "lic static void")).isEqualTo(CARET + "private static void")
        assertThat(execute("protected Str" + CARET + "ing")).isEqualTo("protected Str" + CARET + "ing")
    }

    fun testOptionalMethods() {
        assertThat(execute("opt$CARET.isPresent()")).isEqualTo("opt$CARET.isEmpty()")
        assertThat(execute("opt$CARET.isEmpty()")).isEqualTo("opt$CARET.isPresent()")

        // Test different caret positions
        assertThat(execute("opt.is" + CARET + "Present()")).isEqualTo("opt$CARET.isEmpty()")
        assertThat(execute("opt.isEmpty($CARET)")).isEqualTo("opt.isEmpty($CARET)")
    }

    fun testCollectionTypes() {
        assertThat(execute("new " + CARET + "ArrayList<>()")).isEqualTo("new " + CARET + "HashSet<>()")
        assertThat(execute("new " + CARET + "HashSet<>()")).isEqualTo("new " + CARET + "ArrayList<>()")

        // Test different caret positions
        assertThat(execute("new Array" + CARET + "List<>()")).isEqualTo("new " + CARET + "HashSet<>()")
        assertThat(execute("new HashSet<$CARET>()")).isEqualTo("new HashSet<$CARET>()")
    }

    fun testBasicTypes() {
        assertThat(execute(CARET + "int value")).isEqualTo(CARET + "long value")
        assertThat(execute(CARET + "long value")).isEqualTo(CARET + "float value")
        assertThat(execute(CARET + "float value")).isEqualTo(CARET + "double value")
        assertThat(execute(CARET + "double value")).isEqualTo(CARET + "boolean value")

        // Test different caret positions
        assertThat(execute("in" + CARET + "t value")).isEqualTo(CARET + "long value")
        assertThat(execute("double val" + CARET + "ue")).isEqualTo("double val" + CARET + "ue")
    }

    fun testFlowControl() {
        assertThat(execute(CARET + "if (condition)")).isEqualTo(CARET + "else if (condition)")
        assertThat(execute(CARET + "else if(condition)")).isEqualTo(CARET + "if(condition)")

        // Test different caret positions
        assertThat(execute("i" + CARET + "f (condition)")).isEqualTo(CARET + "else if (condition)")
        assertThat(execute("else if (conditio" + CARET + "n)"))
            .isEqualTo("else if (conditio" + CARET + "n)")
    }

    fun testLoopTypes() {
        assertThat(execute(CARET + "for (int i = 0; i < 10; i++)"))
            .isEqualTo(CARET + "while (int i = 0; i < 10; i++)")
        assertThat(execute(CARET + "while (condition)")).isEqualTo(CARET + "for (condition)")

        // Test different caret positions
        assertThat(execute("fo" + CARET + "r (int i = 0; i < 10; i++)"))
            .isEqualTo(CARET + "while (int i = 0; i < 10; i++)")
        assertThat(execute("while (conditio" + CARET + "n)"))
            .isEqualTo("while (conditio" + CARET + "n)")
    }

    fun testLoopControl() {
        assertThat(execute("    " + CARET + "break;")).isEqualTo("    " + CARET + "continue;")
        assertThat(execute("    " + CARET + "continue;")).isEqualTo("    " + CARET + "break;")

        // Test different caret positions
        assertThat(execute("    bre" + CARET + "ak;")).isEqualTo("    " + CARET + "continue;")
        assertThat(execute("    continue$CARET;")).isEqualTo("    continue$CARET;")
    }

    fun testCollectionOperations() {
        assertThat(execute("list$CARET.add(item)")).isEqualTo("list$CARET.remove(item)")
        assertThat(execute("list$CARET.remove(item)")).isEqualTo("list$CARET.add(item)")

        // Test different caret positions
        assertThat(execute("list.a" + CARET + "dd(item)")).isEqualTo("list$CARET.remove(item)")
        assertThat(execute("list.remove(ite" + CARET + "m)"))
            .isEqualTo("list.remove(ite" + CARET + "m)")
    }

    fun testStringCaseMethods() {
        assertThat(execute("str$CARET.toUpperCase()")).isEqualTo("str$CARET.toLowerCase()")
        assertThat(execute("str$CARET.toLowerCase()")).isEqualTo("str$CARET.toUpperCase()")

        // Test different caret positions
        assertThat(execute("str.toUpper" + CARET + "Case()")).isEqualTo("str$CARET.toLowerCase()")
        assertThat(execute("str.toLowerCase($CARET)")).isEqualTo("str.toLowerCase($CARET)")
    }

    fun testMultipleOccurrencesChoosesClosestToCursor() {
        assertThat(execute("assertTrue(x); " + CARET + "assertTrue(y)"))
            .isEqualTo("assertTrue(x); " + CARET + "assertFalse(y)")
        assertThat(execute("list.add(x); " + CARET + "list.add(y)"))
            .isEqualTo("list.add(x); list$CARET.remove(y)")
        assertThat(execute("public void method1() {} " + CARET + "public void method2()"))
            .isEqualTo("public void method1() {} " + CARET + "private void method2()")
    }

    fun testNoMatchForPartialWords() {
        // Should not match parts of larger words
        assertThat(execute("mylist" + CARET + "implementation"))
            .isEqualTo("mylist" + CARET + "implementation")
        assertThat(execute("something" + CARET + "public")).isEqualTo("something" + CARET + "public")
        assertThat(execute("prefix_" + CARET + "int_suffix"))
            .isEqualTo("prefix_" + CARET + "int_suffix")
    }

    fun testWithSurroundingPunctuation() {
        assertThat(execute("result = [" + CARET + "new ArrayList<>()]"))
            .isEqualTo("result = [new " + CARET + "HashSet<>()]")
        assertThat(execute("types = {'" + CARET + "int': 42}"))
            .isEqualTo("types = {'" + CARET + "long': 42}")
        assertThat(execute("obj$CARET.add(item);")).isEqualTo("obj$CARET.remove(item);")
    }

    fun testMethodCallsInChains() {
        assertThat(execute("str.trim()$CARET.toUpperCase().replace(' ', '_')"))
            .isEqualTo("str.trim()$CARET.toLowerCase().replace(' ', '_')")
        assertThat(execute("list.stream()$CARET.add(item)"))
            .isEqualTo("list.stream()$CARET.remove(item)")
    }

    fun testComplexExpressions() {
        // Test within complex expressions
        assertThat(execute("if (obj $CARET== null && obj.length() > 0)"))
            .isEqualTo("if (obj $CARET!= null && obj.length() > 0)")
        assertThat(execute("return items.stream().filter(item -> item $CARET!= null)"))
            .isEqualTo("return items.stream().filter(item -> item $CARET== null)")
        assertThat(execute("result = str$CARET.toLowerCase() != null ? str : \"\""))
            .isEqualTo("result = str$CARET.toUpperCase() != null ? str : \"\"")
    }

    fun testStreamTransformations() {
        // Test map / flatMap
        assertThat(execute("stream$CARET.map(x -> x * 2)"))
            .isEqualTo("stream$CARET.flatMap(x -> x * 2)")
        assertThat(execute("stream$CARET.flatMap(x -> x.stream())"))
            .isEqualTo("stream$CARET.map(x -> x.stream())")

        // Test different caret positions
        assertThat(execute("stream.ma" + CARET + "p(func)")).isEqualTo("stream$CARET.flatMap(func)")
        assertThat(execute("stream.flatMap(func$CARET)")).isEqualTo("stream.flatMap(func$CARET)")
    }

    fun testStreamFiltering() {
        // Test filter / peek
        assertThat(execute("stream$CARET.filter(x -> x > 0)"))
            .isEqualTo("stream$CARET.peek(x -> x > 0)")
        assertThat(execute("stream$CARET.peek(System.out::println)"))
            .isEqualTo("stream$CARET.filter(System.out::println)")

        // Test different caret positions
        assertThat(execute("stream.fil" + CARET + "ter(predicate)"))
            .isEqualTo("stream$CARET.peek(predicate)")
        assertThat(execute("stream.peek(action$CARET)")).isEqualTo("stream.peek(action$CARET)")
    }

    fun testStreamFinders() {
        // Test findAny / findFirst
        assertThat(execute("stream$CARET.findAny()")).isEqualTo("stream$CARET.findFirst()")
        assertThat(execute("stream$CARET.findFirst()")).isEqualTo("stream$CARET.findAny()")

        // Test different caret positions
        assertThat(execute("stream.find" + CARET + "Any()")).isEqualTo("stream$CARET.findFirst()")
        assertThat(execute("stream.findFirst($CARET)")).isEqualTo("stream.findFirst($CARET)")
    }

    fun testStreamMatching() {
        // Test anyMatch / allMatch / noneMatch cycle
        assertThat(execute("stream$CARET.anyMatch(predicate)"))
            .isEqualTo("stream$CARET.allMatch(predicate)")
        assertThat(execute("stream$CARET.allMatch(predicate)"))
            .isEqualTo("stream$CARET.noneMatch(predicate)")
        assertThat(execute("stream$CARET.noneMatch(predicate)"))
            .isEqualTo("stream$CARET.anyMatch(predicate)")

        // Test different caret positions
        assertThat(execute("stream.any" + CARET + "Match(x -> x > 5)"))
            .isEqualTo("stream$CARET.allMatch(x -> x > 5)")
        assertThat(execute("stream.allMatch(predicate$CARET)"))
            .isEqualTo("stream.allMatch(predicate$CARET)")
        assertThat(execute("stream.noneMatch(x -> x < 0$CARET)"))
            .isEqualTo("stream.noneMatch(x -> x < 0$CARET)")
    }

    fun testStreamMethodChaining() {
        // Test streams in method chains
        assertThat(execute("list.stream()$CARET.filter(x -> x > 0).collect(toList())"))
            .isEqualTo("list.stream()$CARET.peek(x -> x > 0).collect(toList())")
        assertThat(execute("data.stream().map(transform)$CARET.findFirst()"))
            .isEqualTo("data.stream().map(transform)$CARET.findAny()")
        assertThat(execute("items.stream().filter(predicate)$CARET.anyMatch(condition)"))
            .isEqualTo("items.stream().filter(predicate)$CARET.allMatch(condition)")
    }

    fun testComplexStreamExpressions() {
        // Test within complex stream operations
        assertThat(execute("return list.stream()$CARET.map(Item::getName).collect(toSet())"))
            .isEqualTo("return list.stream()$CARET.flatMap(Item::getName).collect(toSet())")
        assertThat(execute("boolean exists = stream$CARET.anyMatch(x -> x.isValid())"))
            .isEqualTo("boolean exists = stream$CARET.allMatch(x -> x.isValid())")
        assertThat(
            execute("Optional<String> result = stream$CARET.filter(Objects::nonNull).findFirst()")
        )
            .isEqualTo("Optional<String> result = stream$CARET.peek(Objects::nonNull).findFirst()")
    }

    fun testStreamWithLambdas() {
        // Test with various lambda expressions
        assertThat(execute("stream$CARET.map(x -> x.toString())"))
            .isEqualTo("stream$CARET.flatMap(x -> x.toString())")
        assertThat(execute("stream$CARET.filter(item -> item.getPrice() > 100)"))
            .isEqualTo("stream$CARET.peek(item -> item.getPrice() > 100)")
        assertThat(execute("stream$CARET.allMatch(String::isEmpty)"))
            .isEqualTo("stream$CARET.noneMatch(String::isEmpty)")
        assertThat(execute("stream$CARET.findAny().orElse(null)"))
            .isEqualTo("stream$CARET.findFirst().orElse(null)")
    }

    fun testNestedStreamOperations() {
        // Test with nested stream operations
        assertThat(
            execute(
                "list.stream().map(item -> item.getChildren().stream()$CARET.filter(child -> child.isActive()))"
            )
        )
            .isEqualTo(
                "list.stream().map(item -> item.getChildren().stream()" +
                        CARET +
                        ".peek(child -> child.isActive()))"
            )
        assertThat(execute("data.parallelStream()$CARET.flatMap(Collection::stream).collect(toList())"))
            .isEqualTo("data.parallelStream()" + CARET + ".map(Collection::stream).collect(toList())")
    }

    fun testStreamTerminalOperations() {
        // Test that terminal operations don't interfere with intermediate operations
        assertThat(execute("stream$CARET.map(func).collect(Collectors.toList())"))
            .isEqualTo("stream$CARET.flatMap(func).collect(Collectors.toList())")
        assertThat(execute("stream.filter(pred)$CARET.anyMatch(condition).toString()"))
            .isEqualTo("stream.filter(pred)" + CARET + ".allMatch(condition).toString()")
        assertThat(execute("stream.peek(action).collect(toSet())$CARET.findFirst()"))
            .isEqualTo("stream.peek(action).collect(toSet())" + CARET + ".findAny()")
    }
}
