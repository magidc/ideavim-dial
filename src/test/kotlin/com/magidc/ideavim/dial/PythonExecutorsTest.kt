package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.BasicExecutors
import com.magidc.ideavim.dial.executor.impl.PythonExecutors
import org.assertj.core.api.Assertions.assertThat

class PythonExecutorsTest : BaseTest() {
  override fun getDefinitions(): String = PythonExecutors.category + "," + BasicExecutors.category

  fun testComparisonOperators() {
    // == / !=
    assertThat(execute("if a " + CARET + "== b:")).isEqualTo("if a " + CARET + "!= b:")
    assertThat(execute("if a " + CARET + "!= b:")).isEqualTo("if a " + CARET + "== b:")

    // is not / is
    assertThat(execute("if a " + CARET + "is not None:")).isEqualTo("if a " + CARET + "is None:")
    assertThat(execute("if obj " + CARET + "is None:"))
      .isEqualTo("if obj " + CARET + "is not None:")

    // not in / in
    assertThat(execute("if item " + CARET + "not in collection:"))
      .isEqualTo("if item " + CARET + "in collection:")
    assertThat(execute("if key " + CARET + "in dictionary:"))
      .isEqualTo("if key " + CARET + "not in dictionary:")

    // Test with different caret positions
    assertThat(execute("if a =" + CARET + "= b:")).isEqualTo("if a " + CARET + "!= b:")
    assertThat(execute("if a i" + CARET + "s None:")).isEqualTo("if a " + CARET + "is not None:")
    assertThat(execute("if x n" + CARET + "ot in lst:")).isEqualTo("if x " + CARET + "in lst:")
  }

  fun testCollectionTypes() {
    assertThat(execute("data = " + CARET + "list()")).isEqualTo("data = " + CARET + "tuple()")
    assertThat(execute("data = " + CARET + "tuple()")).isEqualTo("data = " + CARET + "set()")
    assertThat(execute("data = " + CARET + "set()")).isEqualTo("data = " + CARET + "dict()")
    assertThat(execute("data = " + CARET + "dict()")).isEqualTo("data = " + CARET + "list()")
  }

  fun testBasicTypes() {
    assertThat(execute("value = " + CARET + "int(x)")).isEqualTo("value = " + CARET + "float(x)")
    assertThat(execute("value = " + CARET + "float(x)")).isEqualTo("value = " + CARET + "str(x)")
    assertThat(execute("value = " + CARET + "str(x)")).isEqualTo("value = " + CARET + "bool(x)")
    assertThat(execute("value = " + CARET + "bool(x)")).isEqualTo("value = " + CARET + "int(x)")

    // Test with different caret positions
    assertThat(execute("i" + CARET + "nt(\"123\")")).isEqualTo(CARET + "float(\"123\")")
    assertThat(execute("floa" + CARET + "t(42)")).isEqualTo(CARET + "str(42)")
  }

  fun testFlowControl() {
    assertThat(execute(CARET + "if condition:")).isEqualTo(CARET + "elif condition:")
    assertThat(execute(CARET + "elif condition:")).isEqualTo(CARET + "if condition:")

    // Test with different caret positions
    assertThat(execute("i" + CARET + "f x > 0:")).isEqualTo(CARET + "elif x > 0:")
    assertThat(execute("eli" + CARET + "f x > 0:")).isEqualTo(CARET + "if x > 0:")
  }

  fun testLoopTypes() {
    assertThat(execute(CARET + "for item in items:")).isEqualTo(CARET + "while item in items:")
    assertThat(execute(CARET + "while condition:")).isEqualTo(CARET + "for condition:")

    // Test with different caret positions
    assertThat(execute("fo" + CARET + "r i in range(10):"))
      .isEqualTo(CARET + "while i in range(10):")
    assertThat(execute("whi" + CARET + "le True:")).isEqualTo(CARET + "for True:")
  }

  fun testLoopControl() {
    assertThat(execute("    " + CARET + "break")).isEqualTo("    " + CARET + "continue")
    assertThat(execute("    " + CARET + "continue")).isEqualTo("    " + CARET + "break")

    // Test in different contexts
    assertThat(execute("if found: " + CARET + "break")).isEqualTo("if found: " + CARET + "continue")
    assertThat(execute("if not ready: " + CARET + "continue"))
      .isEqualTo("if not ready: " + CARET + "break")
  }

  fun testMethods() {
    assertThat(execute(CARET + "def function():")).isEqualTo(CARET + "async def function():")
    assertThat(execute(CARET + "async def function():")).isEqualTo(CARET + "def function():")

    // Test with different caret positions
    assertThat(execute("de" + CARET + "f my_function():"))
      .isEqualTo(CARET + "async def my_function():")
    assertThat(execute("async de" + CARET + "f handler():")).isEqualTo(CARET + "def handler():")
  }

  fun testAsyncKeywords() {
    assertThat(execute(CARET + "with open('file') as f:"))
      .isEqualTo(CARET + "async with open('file') as f:")
    assertThat(execute(CARET + "async with aiofiles.open('file') as f:"))
      .isEqualTo(CARET + "with aiofiles.open('file') as f:")

    // Test with different caret positions
    assertThat(execute("wi" + CARET + "th context_manager():"))
      .isEqualTo(CARET + "async with context_manager():")
  }

  fun testClassDecorators() {
    assertThat(execute(CARET + "@property")).isEqualTo(CARET + "@classmethod")
    assertThat(execute(CARET + "@classmethod")).isEqualTo(CARET + "@staticmethod")
    assertThat(execute(CARET + "@staticmethod")).isEqualTo(CARET + "@property")

    // Test with different caret positions
    assertThat(execute("@proper" + CARET + "ty")).isEqualTo("$CARET@classmethod")
  }

  fun testCollectionOperations() {
    assertThat(execute("lst" + CARET + ".append(item)")).isEqualTo("lst" + CARET + ".extend(item)")
    assertThat(execute("lst" + CARET + ".extend(items)"))
      .isEqualTo("lst" + CARET + ".insert(items)")
    assertThat(execute("lst" + CARET + ".insert(0, item)"))
      .isEqualTo("lst" + CARET + ".remove(0, item)")
    assertThat(execute("lst" + CARET + ".remove(item)")).isEqualTo("lst" + CARET + ".pop(item)")
    assertThat(execute("lst" + CARET + ".pop()")).isEqualTo("lst" + CARET + ".append()")

    // Test with different contexts
    assertThat(execute("items" + CARET + ".append('new')"))
      .isEqualTo("items" + CARET + ".extend('new')")
    assertThat(execute("if lst" + CARET + ".remove(x):")).isEqualTo("if lst" + CARET + ".pop(x):")
  }

  fun testStringCaseMethods() {
    assertThat(execute("text" + CARET + ".upper()")).isEqualTo("text" + CARET + ".lower()")
    assertThat(execute("text" + CARET + ".lower()")).isEqualTo("text" + CARET + ".upper()")

    // Test in different contexts
    assertThat(execute("name" + CARET + ".upper().strip()"))
      .isEqualTo("name" + CARET + ".lower().strip()")
    assertThat(execute("return word" + CARET + ".lower()"))
      .isEqualTo("return word" + CARET + ".upper()")
  }

  fun testAssertions() {
    // assertTrue / assertFalse
    assertThat(execute(CARET + "assertTrue(condition)")).isEqualTo(CARET + "assertFalse(condition)")
    assertThat(execute(CARET + "assertFalse(result)")).isEqualTo(CARET + "assertTrue(result)")

    // assertEqual / assertNotEqual
    assertThat(execute(CARET + "assertEqual(a, b)")).isEqualTo(CARET + "assertNotEqual(a, b)")
    assertThat(execute(CARET + "assertNotEqual(x, y)")).isEqualTo(CARET + "assertEqual(x, y)")

    // assertIn / assertNotIn
    assertThat(execute(CARET + "assertIn(item, container)"))
      .isEqualTo(CARET + "assertNotIn(item, container)")
    assertThat(execute(CARET + "assertNotIn(key, dict)")).isEqualTo(CARET + "assertIn(key, dict)")

    // Test with different caret positions
    assertThat(execute("assert" + CARET + "True(x)")).isEqualTo(CARET + "assertFalse(x)")
    assertThat(execute("assertEqu" + CARET + "al(a, b)")).isEqualTo(CARET + "assertNotEqual(a, b)")
  }

  fun testStringQuotes() {
    assertThat(execute("text = " + CARET + "\"hello world\""))
      .isEqualTo("text = " + CARET + "'hello world'")
    assertThat(execute("text = " + CARET + "'hello world'"))
      .isEqualTo("text = " + CARET + "\"hello world\"")

    // Test with complex strings
    assertThat(execute("msg = " + CARET + "\"This is a test\""))
      .isEqualTo("msg = " + CARET + "'This is a test'")
    assertThat(execute("path = " + CARET + "'/home/user/file'"))
      .isEqualTo("path = " + CARET + "\"/home/user/file\"")

    // Test with different caret positions
    assertThat(execute("name = \"Joh" + CARET + "n Doe\""))
      .isEqualTo("name = \"Joh" + CARET + "n Doe\"")
    assertThat(execute("city = 'New Yor" + CARET + "k'"))
      .isEqualTo("city = 'New Yor" + CARET + "k'")
  }

  fun testBooleanValues() {
    assertThat(execute("value = " + CARET + "True")).isEqualTo("value = " + CARET + "False")
    assertThat(execute("value = " + CARET + "False")).isEqualTo("value = " + CARET + "True")

    // Test with different caret positions
    assertThat(execute("result = Tr" + CARET + "ue")).isEqualTo("result = " + CARET + "False")
    assertThat(execute("flag = Fal" + CARET + "se")).isEqualTo("flag = " + CARET + "True")
  }

  fun testLogicalOperators() {
    assertThat(execute("if a " + CARET + "and b:")).isEqualTo("if a " + CARET + "or b:")
    assertThat(execute("if a " + CARET + "or b:")).isEqualTo("if a " + CARET + "and b:")

    // Test with different caret positions
    assertThat(execute("if x a" + CARET + "nd y:")).isEqualTo("if x " + CARET + "or y:")
    assertThat(execute("while condition o" + CARET + "r flag:"))
      .isEqualTo("while condition " + CARET + "and flag:")
  }

  fun testMultipleOccurrencesChoosesClosestToCursor() {
    assertThat(execute("if True and " + CARET + "True:"))
      .isEqualTo("if True and " + CARET + "False:")
    assertThat(execute("list.append(x); " + CARET + "list.append(y)"))
      .isEqualTo("list.append(x); list$CARET.extend(y)")
    assertThat(execute("def func(): pass\n" + CARET + "def other():"))
      .isEqualTo("def func(): pass\n" + CARET + "async def other():")
  }

  fun testNoMatchForPartialWords() {
    // Should not match parts of larger words
    assertThat(execute("mylist" + CARET + "comprehension"))
      .isEqualTo("mylist" + CARET + "comprehension")
    assertThat(execute("something" + CARET + "True")).isEqualTo("something" + CARET + "True")
    assertThat(execute("prefix_" + CARET + "int_suffix"))
      .isEqualTo("prefix_" + CARET + "int_suffix")
  }

  fun testWithSurroundingPunctuation() {
    assertThat(execute("return " + CARET + "True,")).isEqualTo("return " + CARET + "False,")
    assertThat(execute("result = [" + CARET + "list()]"))
      .isEqualTo("result = [" + CARET + "tuple()]")
    assertThat(execute("types = {'" + CARET + "int': 42}"))
      .isEqualTo("types = {'" + CARET + "float': 42}")
    assertThat(execute("obj" + CARET + ".append(item);"))
      .isEqualTo("obj" + CARET + ".extend(item);")
  }

  fun testMethodChaining() {
    assertThat(execute("text.strip()" + CARET + ".upper().replace(' ', '_')"))
      .isEqualTo("text.strip()" + CARET + ".lower().replace(' ', '_')")
    assertThat(execute("data.filter(lambda x: x > 0)" + CARET + ".append(item)"))
      .isEqualTo("data.filter(lambda x: x > 0)" + CARET + ".extend(item)")
  }

  fun testComplexExpressions() {
    // Test within complex expressions
    assertThat(execute("if obj " + CARET + "is not None and len(obj) > 0:"))
      .isEqualTo("if obj " + CARET + "is None and len(obj) > 0:")
    assertThat(execute("return [item " + CARET + "for item in items if item is not None]"))
      .isEqualTo("return [item " + CARET + "while item in items if item is not None]")
    assertThat(execute("result = text$CARET.lower() if text else ''"))
      .isEqualTo("result = text$CARET.upper() if text else ''")
  }

  fun testFunctionDefinitions() {
    assertThat(execute(CARET + "def process_data(data):"))
      .isEqualTo(CARET + "async def process_data(data):")
    assertThat(execute(CARET + "async def fetch_data(url):"))
      .isEqualTo(CARET + "def fetch_data(url):")

    // With decorators
    assertThat(execute("@decorator\n" + CARET + "def method(self):"))
      .isEqualTo("@decorator\n" + CARET + "async def method(self):")
  }

  fun testAsyncContexts() {
    assertThat(execute(CARET + "with session.get(url) as response:"))
      .isEqualTo(CARET + "async with session.get(url) as response:")
    assertThat(execute(CARET + "async with asyncio.Lock():"))
      .isEqualTo(CARET + "with asyncio.Lock():")
  }
}
