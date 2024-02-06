package edu.lispectre;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Token> tokens = Tokenizer.tokenizeEquation("5^2");
        Token token = Parser.parseEquationTokens(tokens);
        System.out.println(token.eval());
    }
}