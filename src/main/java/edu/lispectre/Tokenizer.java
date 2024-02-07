package edu.lispectre;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern tokenizingRegEx = Pattern.compile("\\d*\\.?\\d+|[a-zA-Z]+|[\\^+\\-*/]|\\(|\\)");
    private static final Pattern isNumericRegEx = Pattern.compile("\\d*\\.?\\d+");
    private static final Pattern nestedEquationRegEx = Pattern.compile("\\([^)]*\\)");


    public static ArrayList<Token> tokenizeEquation(String equation){
        final Matcher matcher = tokenizingRegEx.matcher(equation);
        final Matcher nestedMatcher = nestedEquationRegEx.matcher(equation);

        final ArrayList<String> matches = new ArrayList<>();
        final ArrayList<Token> naiveTokens = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group();
            matches.add(match);
        }
        for (int index = 0; index < matches.size(); index++){
            String symbol = matches.get(index);
            Token token;
            switch (symbol){
                case "(":
                    if (!nestedMatcher.find()) {
                        throw new RuntimeException("Paren error.");
                    }
                    String nestedEquation = nestedEquation(matches, index);
                    ArrayList<Token> tokenizedNestedEquation = tokenizeEquation(nestedEquation);
                    token = Parser.parseEquationTokens(tokenizedNestedEquation);
                    index += movePointerAfterNestedEquation(matches, index+1);
                    break;
                case "^":
                    token = new OperatorToken(Operator.EXPONENT);
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

    private static String nestedEquation(final ArrayList<String> matches, final int index){
        final StringBuilder nestedEquationBuilder = new StringBuilder();
        int charPointer = index+1;
        for (int unmatchedParentheses = 1; unmatchedParentheses > 0;){
            String symbol = matches.get(charPointer++);
            if (symbol.equals(")")){
                if (unmatchedParentheses > 1){
                    nestedEquationBuilder.append(symbol);
                }
                unmatchedParentheses--;
            }
            else {
                if (symbol.equals("(")){
                    unmatchedParentheses++;
                }
                nestedEquationBuilder.append(symbol);
            }
        }
        return nestedEquationBuilder.toString();
    }
    private static int movePointerAfterNestedEquation(final ArrayList<String> matches, final int pointer) {
        int unmatchedParenthesis = 1;
        int i;

        for (i = pointer; unmatchedParenthesis > 0; i++) {
            String match = matches.get(i);
            if (match.equals("(")) {
                unmatchedParenthesis++;
            }
            if (match.equals(")")) {
                unmatchedParenthesis--;
            }
        }
        return i - pointer;
    }
}
