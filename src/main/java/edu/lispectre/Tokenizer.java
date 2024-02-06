package edu.lispectre;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern tokenizingRegEx = Pattern.compile("-?\\d*\\.?\\d+|[a-zA-Z]+|[+\\-*/]|\\(|\\)");
    private static final Pattern isNumericRegEx = Pattern.compile("-?\\d*\\.?\\d+");
    private static final Pattern nestedEquationRegEx = Pattern.compile("\\((.*)");

    public static ArrayList<Token> tokenizeEquation(String equation){
        final Matcher matcher = tokenizingRegEx.matcher(equation);
        final ArrayList<String> matches = new ArrayList<>();
        final ArrayList<Token> naiveTokens = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group();
            matches.add(match);
        }
        for (String symbol : matches){
            Token token;
            switch (symbol){
                case "(":
                    Matcher nestedMatcher = nestedEquationRegEx.matcher(equation);
                    if (!nestedMatcher.find()) {
                        throw new RuntimeException("Paren error.");
                    }
                    String nestedEquation = nestedEquation(nestedMatcher.group());
                    token = Parser.parseEquationTokens(tokenizeEquation(nestedEquation));
                    break;
                case "*":
                    token = new OperatorToken(Operator.MULTIPLICATION);
                    break;
                case "/":
                    token = new OperatorToken(Operator.DIVISION);
                    break;
                case "+":
                    token = new OperatorToken(Operator.ADDITION);
                    break;
                case "-":
                    token = new OperatorToken(Operator.SUBTRACTION);
                    break;
                default:
                    if (isNumericRegEx.matcher(symbol).matches()){
                        token = new ValueToken(Float.parseFloat(symbol));
                    }
                    else {
                        token = new VariableToken(symbol);
                    }
                    break;

            }
            naiveTokens.add(token);
        }
        return naiveTokens;
    }

    private static String nestedEquation(final String equation){
        final StringBuilder nestedEquationBuilder = new StringBuilder();
        int charPointer = 1;
        for (int unmatchedParentheses = 1; unmatchedParentheses > 0;){
            char c = equation.charAt(charPointer++);
            if (c == ')'){
                if (unmatchedParentheses > 1){
                    nestedEquationBuilder.append(c);
                }
                unmatchedParentheses--;
            }
            else {
                if (c == '('){
                    unmatchedParentheses++;
                }
                nestedEquationBuilder.append(c);
            }
        }
        return nestedEquationBuilder.toString();
    }
    private static int movePointerAfterNestedEquation(final ArrayList<String> matches) {
        final int matchesAmount = matches.size();
        int leftmostParenthesis = 0;
        int rightmostParenthesis = 0;

        for (int i = 0; i<matchesAmount; i++){
            if (matches.get(i).equals("(")) {
                leftmostParenthesis = i;
                break;
            }
        }
        for (int i = matchesAmount-1; i>=0; i--){
            if (matches.get(i).equals(")")) {
                rightmostParenthesis = i;
                break;
            }
        }

        return rightmostParenthesis-leftmostParenthesis;
    }
}
