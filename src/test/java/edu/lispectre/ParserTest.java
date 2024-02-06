package edu.lispectre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    String simpleEquation;
    String simpleExponentEquation;
    String simpleWithDecimalsEquation;
    String differentiatingPrecedencesEquation;
    String oneParenthesisEquation;
    String multipleParenthesesEquation;
    String nestedParenthesesEquation;

    @BeforeEach
    public void setUp() {
        simpleEquation = "1+2-3";
        simpleExponentEquation = "5^2";
        simpleWithDecimalsEquation = "52.8-12.5*2.5";
        differentiatingPrecedencesEquation = "12*35+5.1/3*2-34";
        oneParenthesisEquation = "2*(30+2)";
        multipleParenthesesEquation = "(25+13)*(26-24)/(12*12)";
        nestedParenthesesEquation = "((5-10)-3)*2";
    }

    @Test
    void parseSimple() {
        assertEquals(0.0f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(simpleEquation)).eval());
    }

    @Test
    void parseSimpleExponent(){
        assertEquals(25.0f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(simpleExponentEquation)).eval());
    }
    @Test
    void parseSimpleDecimals() {
        assertEquals(21.55f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(simpleWithDecimalsEquation)).eval());
    }
    @Test
    void parseDiffPrecedences(){
        assertEquals(389.4f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(differentiatingPrecedencesEquation)).eval());
    }
    @Test
    void parseParentheses(){
        assertEquals(64.0f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(oneParenthesisEquation)).eval());
    }
    @Test
    void parseMultipleParentheses(){
        assertEquals(0.527f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(multipleParenthesesEquation)).eval(), 0.001f);
    }
    @Test
    void nestedParentheses(){
        assertEquals(-16.0f, Parser.parseEquationTokens(Tokenizer.tokenizeEquation(nestedParenthesesEquation)).eval());
    }
}