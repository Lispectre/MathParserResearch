package edu.lispectre.mathparser;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static edu.lispectre.mathparser.Parser.parseEquationTokens;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Tokenizer tokenizer = new Tokenizer();
        String input;
        System.out.println("Input an equation to calculate: ");
        while (true) {
            System.out.print("> ");
            input = in.nextLine();
            if (input.equals("exit")) break;
            if (input.isEmpty()) continue;
            tokenizer.tokenizeEquation(input);
            HashMap<String, Token> variables = tokenizer.getVariables();
            if (!variables.isEmpty()) {
                for (Map.Entry<String, Token> variable : variables.entrySet()) {
                    System.out.print("Value of " + variable.getKey() + ": ");
                    variables.get(variable.getKey()).changeVariable(in.nextDouble());
                }
                in.nextLine();
            }
            System.out.println("= " + parseEquationTokens(tokenizer.getTokens()).eval());
        }
    }
}