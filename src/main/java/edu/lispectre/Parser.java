package edu.lispectre;


import java.util.ArrayList;
import java.util.regex.Matcher;

public class Parser {
    // TODO: Implement parsing methods, return only the token which is the top node of the AST.
    public static Token parseEquationTokens(final ArrayList<Token> naiveTokens){
        final ArrayList<Token> tokens;
        for (Token token : naiveTokens){
            // Order of precedence:
            // Parentheses (done)
            // TODO: Exponents
            // TODO: Multiplication
            // TODO: Division
            // TODO: Addition
            // TODO: Subtraction
        }
        return new ValueToken(0.0f); // temp
    }
}
