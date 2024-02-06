package edu.lispectre;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Token> tokens = Tokenizer.tokenizeEquation("123*56--45/21");
        Token token = Parser.parseEquationTokens(tokens);
    }
}