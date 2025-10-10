# Changelog

## 1.1.4

- **Wrong mappings description in the README fixed**

## 1.1.3

- **Performance improvements**

### JavaScript Transformations

| Category                                              | Transformation                                     |
|-------------------------------------------------------|----------------------------------------------------|
| **Named functions / Arrow functions expressions**     | `function name() {}` ↔ `const name = () => {}`     |
| **Arrow functions / Anonymous functions**             | `() => {}` ↔ `function() {}`                       |
| **Anonymous functions expressions / Named functions** | `const name = function(){}` ↔ `function name() {}` |
| **Variable Declarations**                             | `let` ↔ `var` ↔ `const`                            |

*Note: Function transformations preserve parameter lists and async keywords when present.*


### TypeScript Transformations

| Category             | Transformation                                                                    |
|----------------------|-----------------------------------------------------------------------------------|
| **Basic Types**      | `string` ↔ `number` ↔ `boolean` ↔ `object` ↔ `any` ↔ `unknown` ↔ `never` ↔ `void` |
| **Utility Types**    | `Partial` ↔ `Required` ↔ `Readonly` ↔ `Pick` ↔ `Omit` ↔ `Record`                  |
| **Access Modifiers** | `public` ↔ `private` ↔ `protected` ↔ `readonly`                                   |



## 1.1.2

- **Version codes increment bug fix**

## 1.1.1

- **Minor fixes**

## 1.1.0

- **Smart version number decrementing**: It takes into consideration the scale of the last number: 10.2.00 → 10.1.99
- **Various internal improvements**

## 1.0.0

### Built-in Text Transformations

- **Numeric Values**: Increment/decrement integers, decimals, and scientific notation
- **Boolean Values**: Toggle between `true`/`false`
- **Logical Operators**: Switch between `&&`/`||`, `and`/`or`
- **Comparison Operators**: Toggle `==`/`!=`, `is`/`is not`, `in`/`not in`,`>`/`<`
- **Bitwise Operators**: Switch between `&`/`|`
- **Directional Values**: Cycle through `up`/`down`/`left`/`right`
- **Date/Time**: Smart date and time manipulation
- **Language-specific**: Support specific transformations for Java, Python, Rust or Markdown
