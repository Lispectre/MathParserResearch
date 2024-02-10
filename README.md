# Metaphrase*
A Java project (perhaps a library once it gets competent enough) for parsing and interpreting math equations.

## Usage
Currently running the main class brings up a REPL, which can be used for entering equations or running a little benchmark:

![repl_showcase](https://github.com/Lispectre/Metaphrase/assets/55454477/d71bc5d9-91d6-43a3-b7ae-46084a1ae2f3)

In code:
```java
String equation = "((3 - 8 / (1 - 10 * (5)) * 4) / 3) * 8 / 5 / 1";
Tokenizer tokenizer = new Tokenizer(equation);
Token topOfAST = Parser.parseTokens(tokenizer.getTokens());
topOfAST.eval(); // returns 1.9482993197278913
```
## To Dos
- BigDecimal instead of Doubles for accuracy
- more error prone to unorthodox equations (e.g. "2---1" could be evaluated as "2-(-(-1))")
- more operators
- trigonometric functions support

*an anagram for "parse math"
