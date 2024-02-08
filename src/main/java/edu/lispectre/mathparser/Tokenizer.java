package edu.lispectre.mathparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern tokenizingRegEx = Pattern.compile("\\d*\\.?\\d+|[a-zA-Z]+|[\\^+\\-*/]|\\(|\\)");
    private static final Pattern isNumericRegEx = Pattern.compile("\\d*\\.?\\d+");
    private static final Pattern nestedEquationRegEx = Pattern.compile("\\([^)]*\\)");
    private static final ArrayList<String> operators = new ArrayList<>(Arrays.asList("+", "(", "-", "*", "/"));
    private final ArrayList<Token> unparsedTokens = new ArrayList<>();
    private final HashMap<String, Token> variableAccess = new HashMap<>();

    Tokenizer() {
    }

    Tokenizer(String equation) {
        tokenizeEquation(equation);
    }

    public void tokenizeEquation(String equation) {
        final Matcher matcher = tokenizingRegEx.matcher(equation);
        final Matcher nestedMatcher = nestedEquationRegEx.matcher(equation);
        final ArrayList<String> matches = new ArrayList<>();

        unparsedTokens.clear();
        variableAccess.clear();

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
                        throw new RuntimeException("Parentheses error - unmatched open parenthesis.");
                    }
                    String nestedEquation = nestedEquation(matches, index);
                    Tokenizer nestedEquationTokenizer = new Tokenizer(nestedEquation);
                    ArrayList<Token> nestedEquationTokens = nestedEquationTokenizer.getTokens();
                    this.variableAccess.putAll(nestedEquationTokenizer.getVariables());
                    token = Parser.parseEquationTokens(nestedEquationTokens);
                    index += movePointerAfterNestedEquation(matches, index+1);
                    break;
                case ")":
                    throw new RuntimeException("Parentheses error - unmatched closing parenthesis.");
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
                    if (index == 0 || operators.contains(matches.get(index - 1))) {
                        token = new OperatorToken(Operator.UNARYMINUS);
                    } else {
                        token = new OperatorToken(Operator.SUBTRACTION);
                    }
                    break;
                default:
                    if (isNumericRegEx.matcher(symbol).matches()){
                        token = new ValueToken(Double.parseDouble(symbol));
                    }
                    else {
                        if (!variableAccess.containsKey(symbol)) {
                            token = new VariableToken(symbol);
                            variableAccess.put(symbol, token);
                        } else {
                            token = variableAccess.get(symbol);
                        }
                    }
                    break;
            }
            unparsedTokens.add(token);
        }
    }

    public HashMap<String, Token> getVariables() {
        return variableAccess;
    }

    public void setVariable(String identifier, double value) {
        variableAccess.get(identifier).changeVariable(value);
    }

    public void setVariables(String[] identifiers, double[] values) {
        if (identifiers.length != values.length) {
            throw new InputMismatchException("The amount of variable identifiers and values does not match.");
        }
        for (int i = 0; i < identifiers.length; i++) {
            this.setVariable(identifiers[i], values[i]);
        }
    }

    public ArrayList<Token> getTokens() {
        return unparsedTokens;
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
