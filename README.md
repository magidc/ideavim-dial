<!-- Plugin description -->

# IdeaVim Dial

IdeaVim Dial is a JetBrains IDE plugin that extends IdeaVim with advanced text increment and decrement functionality. The plugin provides intelligent pattern matching and transformation capabilities for various text elements including numbers, dates, boolean values, operators, and programming language-specific keywords.

## What is IdeaVim Dial?

IdeaVim Dial enhances the standard increment/decrement functionality found in Vim editors by adding support for complex text patterns beyond simple numbers. The plugin can cycle through related values, toggle between opposite states, and intelligently manipulate various text formats commonly found in code.

The plugin operates by analyzing text near the cursor position and applying contextually appropriate transformations. It supports both forward and reverse cycling through predefined sets of values, making it possible to quickly alternate between related concepts without manual text editing.

## Features
- **Vim-like Behavior**: Increment or decrement numbers just like Vim's `Ctrl+A`/`Ctrl+X`
- **Search from cursor**: Transforms the first matching word found from the cursor position forward within the current line
- **Works within words**: Cursor can be also within the target word
- **Customizable**: Define your own sets of transformations and enable them in your `.ideavimrc`

### Built-in Text Transformations

- **Numeric Values**: Increment/decrement integers, decimals, and scientific notation
- **Boolean Values**: Toggle between `true`/`false`, `True`/`False`
- **Logical Operators**: Switch between `&&`/`||`, `and`/`or`, `AND`/`OR`
- **Comparison Operators**: Toggle `==`/`!=`, `is`/`is not`, `>`/`<`, `>=`/`<=`
- **Bitwise Operators**: Switch between `&`/`|`
- **Directional Values**: Cycle through `up`/`down`/`left`/`right`
- **Quote Styles**: Rotate between `"string"`, `'string'`, `↔` &#96;string&#96;
- **Date/Time**: Smart date and time manipulation
- **Language-specific**: Support specific transformations
  for [Java](#java-transformations), [Python](#python-transformations), [JavaScript](#javascript-transformations), [Rust](#rust-transformations)
  or [Markdown](#markdown-transformations)

### Smart Context Awareness

- **Cursor Position Sensitive**: Works regardless of where your cursor is within or before the target text
- **Word Boundary Recognition**: Distinguishes between whole words and partial matches
- **Case Preservation**: Maintains original case when transforming text
- **Multiple Matches**: Automatically selects the closest match to your cursor

### Requirements

- IntelliJ IDEA 2025.1+
- IdeaVim plugin 2.27.0+
- Java 17+

### Installation
#### Plugin Marketplace Installation
1. Ensure you have the IdeaVim plugin installed and enabled
2. Install the plugin from the IntelliJ IDEA Plugin Marketplace
3. Activate the plugin in your `.ideavimrc`
4. Restart IntelliJ IDEA

#### Manual Installation
1. Download the latest `.zip` file release from the [releases page](https://github.com/magidc/ideavim-dial/releases)
2. Open IDE Settings:
    - Go to **File** → **Settings** (Windows/Linux) or **IntelliJ IDEA** → **Preferences** (macOS)
    - Or press `Ctrl+Alt+S` (Windows/Linux) or `Cmd+,` (macOS)
3. Navigate to Plugins:
    - Select **Plugins** from the left sidebar
4. Install from disk:
    - Click the **⚙️ gear icon** → **Install Plugin from Disk...**
    - Browse to your plugin's `.zip` file
    - Select the file and click **OK**
5. Activate the plugin in your `.ideavimrc`
6. Restart IntelliJ IDEA


### Configuration

Configure which transformation groups to enable in your `.ideavimrc`:

```vimscript
" Activate plugin
set dial

" Enable multiple groups
let g:dial_include = "basic,numbers,dates"

" Enable some categories (basic, numbers, dates, java) and some specific transformations (python:async, markdown:task_item)
let g:dial_include = "basic,numbers,dates,java,python:async,markdown:task_item"

" Custom transformations
let g:dial_custom_definitions = [
    ['normalizedCaseWords', ['one', 'two', 'three']],
    ['words', ['un', 'deux', 'trois']],
    ['normalizedCasePattern', ['alpha', 'beta', 'gamma']],
    ['pattern', ['start', 'middle', 'end']]
]
```

### Recommended key mappings

Add these mappings to your `.ideavimrc`:

```vimscript
" Map Ctrl+A to increment
nmap <C-a> <Plug>(DialIncrement)

" Map Ctrl+X to decrement  
nmap <C-x> <Plug>(DialDecrement)
```

## Available Groups

#### If not specified, basic, number, and date groups are enabled by default. Also, specific language groups are enabled by default based on the JetBrains application in use. For example, IntelliJ IDEA will enable Java specific transformations, Pycharm will enable Python transformations, etc.

- `basic`: Boolean values, operators, directions, quotes
- `numbers`: Integer, decimal, and scientific notation
- `dates`: Date and time patterns
- `java`: Java-specific patterns (visibility, basic types, collections methods, streams, etc.)
- `python`: Python-specific patterns (basic types, loops, collections, etc.)
- `javascript`: JavaScript-specific patterns
- `markdown`: Markdown formatting
- `rust`: Rust-specific patterns


<!-- Plugin description end -->

## Built-in Transformations

### Basic Transformations

| Category              | Transformation                      |
|-----------------------|-------------------------------------|
| **Boolean Values**    | `true` ↔ `false`                    |
|                       | `True` ↔ `False`                    |
| **Logical Operators** | `and` ↔ `or`                        |
|                       | `AND` ↔ `OR`                        |
|                       | `&&` ↔ `\|\|`                       |
| **Comparison**        | `==` ↔ `!=`                         |
|                       | `is` ↔ `is not`                     |
|                       | `>` ↔ `<`                           |
|                       | `>=` ↔ `<=`                         |
| **Directional Words** | `up` ↔ `down` ↔ `left` ↔ `right`    |
| **String Quotes**     | `"text"` ↔ `'text'` ↔ \`text\`      |
| **HTTP Methods**      | `GET` ↔ `POST` ↔ `PUT` ↔ `DELETE`   |
| **Log Levels**        | `DEBUG` ↔ `INFO` ↔ `WARN` ↔ `ERROR` |

### Numeric Transformations

| Category                | Before             | After (Increment)  | After (Decrement)  | Description                            |
|-------------------------|--------------------|--------------------|--------------------|----------------------------------------|
| **Unsigned Integers**   | `42`               | `43`               | `41`               | Positive whole numbers                 |
|                         | `0`                | `1`                | `-1`               | Zero becomes negative when decremented |
|                         | `999`              | `1000`             | `998`              | Large integers                         |
| **Signed Integers**     | `+15`              | `+16`              | `+14`              | Explicitly positive integers           |
|                         | `-5`               | `-4`               | `-6`               | Negative integers                      |
|                         | `-1`               | `0`                | `-2`               | Negative to zero transition            |
| **Unsigned Decimals**   | `3.14`             | `3.15`             | `3.13`             | Decimal precision maintained           |
|                         | `1.0`              | `1.1`              | `0.9`              | Single decimal place                   |
|                         | `10.123`           | `10.124`           | `10.122`           | Multiple decimal places                |
|                         | `0.001`            | `0.002`            | `0.000`            | Small increments                       |
| **Signed Decimals**     | `+2.5`             | `+2.6`             | `+2.4`             | Explicitly positive decimals           |
|                         | `-1.75`            | `-1.74`            | `-1.76`            | Negative decimals                      |
|                         | `-0.1`             | `0.0`              | `-0.2`             | Crossing zero boundary                 |
| **Scientific Notation** | `1e2`              | `2e2`              | `0e2`              | Integer base with exponent             |
|                         | `1E-3`             | `2E-3`             | `0E-3`             | Uppercase E notation                   |
|                         | `2.5e10`           | `3.5e10`           | `1.5e10`           | Decimal base with exponent             |
|                         | `-1.0E-2`          | `-0.0E-2`          | `-2.0E-2`          | Negative scientific notation           |
| **Version Codes**       | `1.2.3`            | `1.2.4`            | `1.2.2`            | Standard semantic versioning           |
|                         | `10.23.4-SNAPSHOT` | `10.23.5-SNAPSHOT` | `10.23.3-SNAPSHOT` | With suffix                            |
|                         | `2.1.0.RC1`        | `2.1.1.RC1`        | `2.0.9.RC1`        | Release candidate                      |
|                         | `1.0_beta`         | `1.1_beta`         | `0.9_beta`         | Underscore separator                   |


### Date & Time Transformations

| Format                   | Example                | Increment Unit |
|--------------------------|------------------------|----------------|
| **ISO Date**             | `2023-12-25`           | Days           |
| **European Date**        | `25.12.2023`           | Days           |
| **US Date**              | `12/25/2023`           | Days           |
| **Time 24h**             | `14:30`                | Minutes        |
| **Time with Seconds**    | `14:30:45`             | Seconds        |
| **Time 12h**             | `2:30 PM`              | Minutes        |
| **ISO DateTime**         | `2023-12-25T14:30:45`  | Seconds        |
| **ISO DateTime with TZ** | `2023-12-25T14:30:45Z` | Seconds        |

### Java Transformations

| Category                  | Transformation                                  |
|---------------------------|-------------------------------------------------|
| **Assertions**            | `assertEquals` ↔ `assertNotEquals`              |
|                           | `assertFalse` ↔ `assertTrue`                    |
|                           | `assertNull` ↔ `assertNotNull`                  |
| **Visibility Modifiers**  | `public` ↔ `private` ↔ `protected`              |
| **Optional Methods**      | `.isPresent` ↔ `.isEmpty`                       |
| **Collection Types**      | `ArrayList` ↔ `HashSet`                         |
| **Data Types**            | `int` ↔ `long` ↔ `float` ↔ `double` ↔ `boolean` |
| **Flow Control**          | `if` ↔ `else if`                                |
| **Loop Types**            | `for` ↔ `while`                                 |
| **Loop Control**          | `break` ↔ `continue`                            |
| **Collection Operations** | `.add` ↔ `.remove`                              |
| **String Case**           | `.toLowerCase` ↔ `.toUpperCase`                 |
| **Streams**               | `.map` ↔ `.flatMap`                             |
|                           | `.filter` ↔ `.peek`                             |
|                           | `.findAny` ↔ `.findFirst`                       |
|                           | `.anyMatch` ↔ `.allMatch` ↔ `.noneMatch`        |
| **Comparison**            | `==` ↔ `!=`                                     |

### Python Transformations

| Category                  | Transformation                                      |
|---------------------------|-----------------------------------------------------|
| **Comparison Operators**  | `==` ↔ `!=`                                         |
|                           | `is` ↔ `is not`                                     |
|                           | `in` ↔ `not in`                                     |
| **Data Types**            | `int` ↔ `float` ↔ `str` ↔ `bool`                    |
| **Flow Control**          | `if` ↔ `elif`                                       |
| **Loop Types**            | `for` ↔ `while`                                     |
| **Loop Control**          | `break` ↔ `continue`                                |
| **Function Definitions**  | `def` ↔ `async def`                                 |
| **Context Managers**      | `with` ↔ `async with`                               |
| **Class Decorators**      | `@property`↔`@classmethod`↔`@staticmethod`          |
| **Collection Types**      | `list(`↔`tuple(`↔`set(`↔`dict(`                     |
| **Collection Operations** | `.append(`↔`.extend(`↔`.insert(`↔`.remove(`↔`.pop(` |
| **String Case**           | `.upper()`↔`.lower()`                               |
| **Assertions**            | `assertFalse` `assertTrue`                          |
|                           | `assertEqual` ↔ `assertNotEqual`                    |
|                           | `assertIn` ↔ `assertNotIn`                          |
| **String Quotes**         | `'text'` ↔ `"text"`                                 |

### JavaScript Transformations

| Category                  | Transformation                                        |
|---------------------------|-------------------------------------------------------|
| **Function Declarations** | `function name() {}` ↔ `const name = () => {}`        |
|                           | `var/let/const name = function()` ↔ `function name()` |
| **Arrow Functions**       | `function() {}` ↔ `() => {}`                          |
|                           | `function(params) {}` ↔ `(params) => {}`              |
|                           | `param => {}` ↔ `function(param) {}`                  |
| **Variable Declarations** | `var` ↔ `let` ↔ `const`                               |

### Markdown Transformations

| Category       | Transformation    |
|----------------|-------------------|
| **Task Lists** | `- [ ]` ↔ `- [x]` |

### Rust Transformations

| Category                 | Transformation                                     |
|--------------------------|----------------------------------------------------|
| **Boolean Values**       | `true` ↔ `false`                                   |
| **Mutability**           | `let mut` ↔ `let`                                  |
| **Visibility**           | `pub` ↔ `pub(crate)` ↔ `pub(super)` ↔ `pub(self)`  |
| **Result Handling**      | `.unwrap()` ↔ `.expect()` ↔ `.unwrap_or_default()` |
| **Option Handling**      | `.some()` ↔ `.none()`                              |
| **Comparison Operators** | `==` ↔ `!=`                                        |
| **Logical Operators**    | `&&` ↔ `\|\|`                                      |
| **Memory Management**    | `Box::new()` ↔ `Rc::new()` ↔ `Arc::new()`          |
| **String Types**         | `&str` ↔ `String`                                  |
| **Integer Types**        | `i32` ↔ `i64` ↔ `u32` ↔ `u64` ↔ `usize` ↔ `isize`  |
| **Float Types**          | `f32` ↔ `f64`                                      |
| **Collection Methods**   | `.iter()` ↔ `.iter_mut()` ↔ `.into_iter()`         |
| **Vector Operations**    | `.push()` ↔ `.pop()`                               |
| **Assertions**           | `assert!` ↔ `assert_eq!` ↔ `assert_ne!`            |
| **Debug/Release**        | `debug_assert!` ↔ `assert!`                        |
| **Match Arms**           | `Some(_)` ↔ `None`                                 |
| **Error Propagation**    | `?` ↔ `.unwrap()`                                  |

## Custom Transformations
Add custom definitions to your file using the following format: `.ideavimrc`
``` vim
let g:dial_custom_definitions = [
    ['normalizedCaseWords', ['one', 'two', 'three']],
    ['words', ['un', 'deux', 'trois']],
    ['normalizedCasePattern', ['alpha', 'beta', 'gamma']],
    ['pattern', ['start', 'middle', 'end']]
]
```
### Function Types
#### `normalizedCaseWords`
- **Case insensitive** matching
- **Word boundaries** required (won't match partial words)
- Example: `One` → → `Three` → `One` `Two`

#### `words`
- **Case sensitive** matching
- **Word boundaries** required (won't match partial words)
- Example: `un` → `deux` → `trois` → `un`

### `normalizedCasePattern`
- **Case insensitive** matching
- **No word boundaries** (matches anywhere in text)
- Example: `Alpha123` → `Beta123` → `Gamma123` → `Alpha123`

### `pattern`
- **Case sensitive** matching
- **No word boundaries** (matches anywhere in text)
- Example: `start_var` → `middle_var` → `end_var` → `start_var`

### Complete Example
``` vimscript
" Define custom word cycling sets
let g:dial_custom_definitions = [
    " HTTP status categories (case insensitive, whole words)
    ['normalizedCaseWords', ['success', 'redirect', 'client_error', 'server_error']],
    
    " Git commands (case sensitive, whole words)
    ['words', ['add', 'commit', 'push', 'pull', 'merge', 'rebase']],
    
    " CSS units (case insensitive, partial matches)
    ['normalizedCasePattern', ['px', 'em', 'rem', '%', 'vh', 'vw']],
    
    " Priority levels (case sensitive, partial matches)  
    ['pattern', ['low', 'medium', 'high', 'critical']]
]
```
## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- Inspired by [dial.nvim](https://github.com/monaqa/dial.nvim) for Neovim and [ideavim-switch](https://github.com/jphalip/ideavim-switch) plugin for JetBrains IDEs
- Built on top of [IdeaVim](https://github.com/JetBrains/ideavim) plugin
