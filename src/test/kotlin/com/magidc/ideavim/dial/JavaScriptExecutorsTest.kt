package com.magidc.ideavim.dial

import com.magidc.ideavim.dial.executor.impl.JavaScriptExecutors
import org.assertj.core.api.Assertions.assertThat

class JavaScriptExecutorsTest : BaseTest() {
    override fun getDefinitions(): String = JavaScriptExecutors.category

    fun testNamedFunctionToArrowFunction() {
        assertThat(execute(CARET + "function hello() {")).isEqualTo(CARET + "const hello = () => {")
        assertThat(execute(CARET + "function add(a, b) {")).isEqualTo(CARET + "const add = (a, b) => {")

        assertThat(execute(CARET + "async function fetchData() {")).isEqualTo(CARET + "const fetchData = async () => {")
        assertThat(execute(CARET + "async function processData(data, options) {")).isEqualTo(CARET + "const processData = async (data, options) => {")

        // Test with different caret positions
        assertThat(execute("func" + CARET + "tion test() {")).isEqualTo(CARET + "const test = () => {")
        assertThat(execute("function te" + CARET + "st(x) {")).isEqualTo(CARET + "const test = (x) => {")
        assertThat(execute("async func" + CARET + "tion handler() {")).isEqualTo(CARET + "const handler = async () => {")
    }

    fun testVariableAssignmentToNamedFunction() {
        // var assignment to function
        assertThat(execute(CARET + "var myFunc = function(")).isEqualTo(CARET + "function myFunc(")
        assertThat(execute(CARET + "var handler = async function(")).isEqualTo(CARET + "async function handler(")

        // let assignment to function
        assertThat(execute(CARET + "let calculate = function(")).isEqualTo(CARET + "function calculate(")
        assertThat(execute(CARET + "let process = async function(")).isEqualTo(CARET + "async function process(")

        // const assignment to function
        assertThat(execute(CARET + "const transform = function(")).isEqualTo(CARET + "function transform(")
        assertThat(execute(CARET + "const fetch = async function(")).isEqualTo(CARET + "async function fetch(")

        // Test with different caret positions
        assertThat(execute("v" + CARET + "ar helper = function(")).isEqualTo(CARET + "function helper(")
        assertThat(execute("let util" + CARET + " = function(")).isEqualTo(CARET + "function util(")
    }

    fun testArrowFunctionToRegularFunction() {
        // Anonymous function with no parameters
        assertThat(execute(CARET + "function() {")).isEqualTo(CARET + "() => {")

        // Anonymous function with parameters
        assertThat(execute(CARET + "function(x, y) {")).isEqualTo(CARET + "(x, y) => {")

        // Arrow function with parentheses back to regular function
        assertThat(execute(CARET + "(a, b) => {")).isEqualTo(CARET + "function(a, b) {")

        // Single parameter arrow function back to regular function
        assertThat(execute(CARET + "item => {")).isEqualTo(CARET + "function(item) {")

        // Test with different caret positions
        assertThat(execute("func" + CARET + "tion(data) {")).isEqualTo(CARET + "(data) => {")
        assertThat(execute("(x, y" + CARET + ") => {")).isEqualTo(CARET + "function(x, y) {")
        assertThat(execute("val" + CARET + "ue => {")).isEqualTo(CARET + "function(value) {")
    }

    fun testEs6Declarations() {
        assertThat(execute(CARET + "let count = 0")).isEqualTo(CARET + "var count = 0")
        assertThat(execute(CARET + "var value = 42")).isEqualTo(CARET + "const value = 42")
        assertThat(execute(CARET + "const PI = 3.14")).isEqualTo(CARET + "let PI = 3.14")

        assertThat(execute("l" + CARET + "et x = 5")).isEqualTo(CARET + "var x = 5")
        assertThat(execute("var y" + CARET + " = 10")).isEqualTo("var y" + CARET + " = 10")
        assertThat(execute("con" + CARET + "st z = 15")).isEqualTo(CARET + "let z = 15")
    }

    fun testComplexFunctionTransformations() {
        assertThat(execute(CARET + "function validateUser(user, options = {}) {")).isEqualTo(CARET + "const validateUser = (user, options = {}) => {")
        assertThat(execute(CARET + "async function processRequest({id, data}) {")).isEqualTo(CARET + "const processRequest = async ({id, data}) => {")
        assertThat(execute(CARET + "(user, {timeout = 5000}) => {")).isEqualTo(CARET + "function(user, {timeout = 5000}) {")
    }

    fun testNoMatchForPartialWords() {
        // Should not match parts of larger words
        assertThat(execute("myfunction" + CARET + "call()")).isEqualTo("myfunction" + CARET + "call()")
        assertThat(execute("constant" + CARET + "value = 5")).isEqualTo("constant" + CARET + "value = 5")
        assertThat(execute("lettering" + CARET + " = text")).isEqualTo("lettering" + CARET + " = text")
    }

    fun testMultipleOccurrencesChoosesClosestToCursor() {
        assertThat(execute("let x = 5; " + CARET + "var y = 10")).isEqualTo("let x = 5; " + CARET + "const y = 10")
        assertThat(execute(CARET + "let a = 1; const b = 2")).isEqualTo(CARET + "var a = 1; const b = 2")
        assertThat(execute("function test() {}; " + CARET + "function other() {")).isEqualTo("function test() {}; ${CARET}const other = () => {")
    }

    fun testWithSurroundingSpacesAndPunctuation() {
        assertThat(execute("if (condition) { " + CARET + "var result = true; }")).isEqualTo("if (condition) { " + CARET + "const result = true; }")
        assertThat(execute("return " + CARET + "function(x) {")).isEqualTo("return " + CARET + "(x) => {")
        assertThat(execute("const handler = " + CARET + "(event) => {")).isEqualTo("const handler = " + CARET + "function(event) {")
    }

    fun testReverseTransformations() {
        // Test reverse direction for arrow functions
        assertThat(execute(CARET + "() => {", true)).isEqualTo(CARET + "function() {")
        assertThat(execute(CARET + "(a, b) => {", true)).isEqualTo(CARET + "function(a, b) {")
        assertThat(execute(CARET + "item => {", true)).isEqualTo(CARET + "function(item) {")

        // Test reverse direction for declarations
        assertThat(execute(CARET + "const value = 42", true)).isEqualTo(CARET + "var value = 42")
        assertThat(execute(CARET + "let count = 0", true)).isEqualTo(CARET + "const count = 0")
        assertThat(execute(CARET + "var name = 'test'", true)).isEqualTo(CARET + "let name = 'test'")

        // Test reverse direction for named functions
        assertThat(execute("const " + CARET + "add = (a, b) => {", true)).isEqualTo(CARET + "function add(a, b) {")
        assertThat(execute("const " + CARET + "fetch = async () => {", true)).isEqualTo(CARET + "async function fetch() {")
    }
}
