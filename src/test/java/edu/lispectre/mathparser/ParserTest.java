package edu.lispectre.mathparser;

import org.junit.jupiter.api.Test;

import static edu.lispectre.mathparser.Parser.parseEquationTokens;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
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
        assertEquals(0.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }

    @Test
    void parseSimpleExponent(){
        tokenizer.tokenizeEquation(simpleExponentEquation);
        assertEquals(25.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }

    @Test
    void parseSubtractingNegativeNumber() {
        tokenizer.tokenizeEquation(subtractingNegativeNumberEquation);
        assertEquals(12.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }
    @Test
    void parseSimpleDecimals() {
        tokenizer.tokenizeEquation(simpleWithDecimalsEquation);
        assertEquals(21.55, parseEquationTokens(tokenizer.getTokens()).eval(), 0.01);
    }
    @Test
    void parseDiffPrecedences(){
        tokenizer.tokenizeEquation(differentiatingPrecedencesEquation);
        assertEquals(389.4, parseEquationTokens(tokenizer.getTokens()).eval());
    }
    @Test
    void parseParentheses(){
        tokenizer.tokenizeEquation(oneParenthesisEquation);
        assertEquals(64.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }
    @Test
    void parseMultipleParentheses(){
        tokenizer.tokenizeEquation(multipleParenthesesEquation);
        assertEquals(0.527, parseEquationTokens(tokenizer.getTokens()).eval(), 0.001);
    }
    @Test
    void parseNestedParentheses() {
        tokenizer.tokenizeEquation(nestedParenthesesEquation);
        assertEquals(-16.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }

    @Test
    void parseAReallyLongAndUnnecessarilyComplicatedEquation() {
        tokenizer.tokenizeEquation(aReallyLongAndUnnecessarilyComplicatedJustLikeThisVariableNameEquation);
        assertEquals(-6255.1865, parseEquationTokens(tokenizer.getTokens()).eval(), 0.0001);
    }

    @Test
    void parseOneKnownVariable() {
        tokenizer.tokenizeEquation(oneVariableEquation);
        tokenizer.setVariable("x", 2.0);
        assertEquals(32.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }

    @Test
    void parseMultipleKnownVariables() {
        tokenizer.tokenizeEquation(multipleVariablesEquation);
//        tokenizer.setVariable("x", 5.0f);
//        tokenizer.setVariable("y", 2.3f);
//        tokenizer.setVariable("z", 10.0f);
        String[] variables = {"x", "y", "z"};
        double[] values = {5.0, 2.3, 10.0};
        tokenizer.setVariables(variables, values);
        assertEquals(14.018, parseEquationTokens(tokenizer.getTokens()).eval(), 0.001);
    }

    @Test
    void parseOneUnknownVariable() {
        tokenizer.tokenizeEquation(oneVariableEquation);
        final double[] preparedValues = {2.0};
        int valuesPointer = 0;
        for (String variable : tokenizer.getVariables().keySet()) {
            tokenizer.setVariable(variable, preparedValues[valuesPointer++]);
        }
        assertEquals(32.0, parseEquationTokens(tokenizer.getTokens()).eval());
    }

    @Test
    void parseMultipleUnknownVariables() {
        tokenizer.tokenizeEquation(multipleVariablesEquation);
        final double[] preparedValues = {5.0, 2.3, 10.0};
        int valuesPointer = 0;
        for (String variable : tokenizer.getVariables().keySet()) {
            tokenizer.setVariable(variable, preparedValues[valuesPointer++]);
        }
        assertEquals(14.018, parseEquationTokens(tokenizer.getTokens()).eval(), 0.001);
    }
}