package edu.lispectre.metaphrase;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static edu.lispectre.metaphrase.Parser.parseTokens;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    final MathContext mc = new MathContext(10);

    final String simpleEquation = "1+2-3";
    final String simpleExponentEquation = "5^2";
    final String subtractingNegativeNumberEquation = "10--2";
    final String simpleWithDecimalsEquation = "52.8-12.5*2.5";
    final String differentiatingPrecedencesEquation = "12*35+5.1/3*2-34";
    final String oneParenthesisEquation = "2*(30+2)";
    final String multipleParenthesesEquation = "(25+13)*(26-24)/(12*12)";
    final String nestedParenthesesEquation = "((5-10)-3)*2";
    final String aReallyLongAndUnnecessarilyComplicatedJustLikeThisVariableNameEquation = "(((3^2 + 4^2) / 5 * 2 + (6 * 9)^(1/2)) - ((7 * 8) / 2 + (10^2) / 11) * ((12 / 13) + (14^3) / 15) + (16 - (17 * 18) / (19^(1/2))) * ((20 + 21) / 22) - ((23 / 24) - 25) * (26 + ((27 * 28)^(1/2)) / 29))";
    final String oneVariableEquation = "2^(x+3)";
    final String multipleVariablesEquation = "30*x/((3-y)+z)";

    final Tokenizer tokenizer = new Tokenizer();



    @Test
    void parseSimple() {
        tokenizer.tokenizeEquation(simpleEquation);
        assertEquals(new BigDecimal("0"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseSimpleExponent() {
        tokenizer.tokenizeEquation(simpleExponentEquation);
        assertEquals(new BigDecimal("25"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseSubtractingNegativeNumber() {
        tokenizer.tokenizeEquation(subtractingNegativeNumberEquation);
        assertEquals(new BigDecimal("12.0"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseSimpleDecimals() {
        tokenizer.tokenizeEquation(simpleWithDecimalsEquation);
        assertEquals(new BigDecimal("21.55", mc), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseDiffPrecedences() {
        tokenizer.tokenizeEquation(differentiatingPrecedencesEquation);
        assertEquals(new BigDecimal("389.4"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseParentheses(){
        tokenizer.tokenizeEquation(oneParenthesisEquation);
        assertEquals(new BigDecimal("64"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }
    @Test
    void parseMultipleParentheses(){
        tokenizer.tokenizeEquation(multipleParenthesesEquation);
        assertEquals(new BigDecimal("0.5277777778", mc), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }
    @Test
    void parseNestedParentheses() {
        tokenizer.tokenizeEquation(nestedParenthesesEquation);
        assertEquals(new BigDecimal("-16"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseAReallyLongAndUnnecessarilyComplicatedEquation() {
        tokenizer.tokenizeEquation(aReallyLongAndUnnecessarilyComplicatedJustLikeThisVariableNameEquation);
        assertEquals(new BigDecimal("-6255.186584"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseOneKnownVariable() {
        tokenizer.tokenizeEquation(oneVariableEquation);
        tokenizer.changeValueOfVariable("x", new BigDecimal("2.0"));
        assertEquals(new BigDecimal("32"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseMultipleKnownVariables() {
        tokenizer.tokenizeEquation(multipleVariablesEquation);
        String[] variables = {"x", "y", "z"};
        BigDecimal[] values = {new BigDecimal("5.0"), new BigDecimal("2.3"), new BigDecimal("10.0")};
        tokenizer.changeValuesOfVariables(variables, values);
        assertEquals(new BigDecimal("14.01869159", mc), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseOneUnknownVariable() {
        tokenizer.tokenizeEquation(oneVariableEquation);
        final BigDecimal[] preparedValues = {new BigDecimal("2.0")};
        int valuesPointer = 0;
        for (String variable : tokenizer.getVariables().keySet()) {
            tokenizer.changeValueOfVariable(variable, preparedValues[valuesPointer++]);
        }
        assertEquals(new BigDecimal("32"), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }

    @Test
    void parseMultipleUnknownVariables() {
        tokenizer.tokenizeEquation(multipleVariablesEquation);
        final BigDecimal[] preparedValues = {new BigDecimal("5.0"), new BigDecimal("2.3"), new BigDecimal("10.0")};
        int valuesPointer = 0;
        for (String variable : tokenizer.getVariables().keySet()) {
            tokenizer.changeValueOfVariable(variable, preparedValues[valuesPointer++]);
        }
        assertEquals(new BigDecimal("14.01869159", mc), parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval());
    }
}