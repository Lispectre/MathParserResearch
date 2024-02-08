package edu.lispectre.metaphrase;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.lispectre.metaphrase.Parser.parseTokens;

public class Main {
    public static void main(String[] args) {
        repl();
    }

    public static void repl() {
        final Scanner in = new Scanner(System.in);
        final Tokenizer tokenizer = new Tokenizer();
        String input;

        System.out.println("Input an equation to calculate (or type 'benchmark [number of equations]'): ");
        while (true) {
            System.out.print("> ");
            input = in.nextLine();
            if (input.toLowerCase().startsWith("exit")) break;
            if (input.toLowerCase().startsWith("benchmark")) {
                Pattern numberRegEx = Pattern.compile("\\d+");
                Matcher matcher = numberRegEx.matcher(input);
                if (!matcher.find()) {
                    System.out.println("No number found. Type 'benchmark [number of equations]'.");
                    continue;
                }
                int numberOfEq = Integer.parseInt(matcher.group());
                Benchmark.startBenchmark(numberOfEq, true);
                continue;
            }
            if (input.isEmpty()) continue;
            tokenizer.tokenizeEquation(input);
            HashMap<String, Token> variables = tokenizer.getVariables();
            if (!variables.isEmpty()) {
                for (Map.Entry<String, Token> variable : variables.entrySet()) {
                    System.out.print("Value of " + variable.getKey() + ": ");
                    double value = in.nextDouble();
                    tokenizer.changeValueOfVariable(variable.getKey(), value);
                }
                in.nextLine();
            }
            Token parsedEquation = parseTokens(tokenizer.getTokens());
            System.out.println("= " + parsedEquation.eval());
        }
    }
}