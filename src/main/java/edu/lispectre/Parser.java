package edu.lispectre;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    // Matches all token material.
    private static final Pattern tokenizingRegEx = Pattern.compile("-?\\d*\\.?\\d+|[a-zA-Z]+|[+\\-*/]|\\(|\\)");
    private static final Pattern isOperatorRegEx = Pattern.compile("[+\\-/*]");
    private static final Pattern isNumericRegEx = Pattern.compile("-?\\d*\\.?\\d+");
    private static final Pattern nestedEquationRegEx = Pattern.compile("\\((.*)");


    // TODO: Implement parsing methods, return only the token which is the top node of the AST.
    public static Token parseEquation(final String equation){
        final Matcher matcher = tokenizingRegEx.matcher(equation);
        final ArrayList<String> matches = new ArrayList<>();
        final ArrayList<Token> tokens = new ArrayList<>();

        System.out.println("Parsing equation: " + equation);

        while (matcher.find()) {
            String match = matcher.group();
            matches.add(match);
        }

        for (int matchPointer = 0; matchPointer<matches.size(); matchPointer++) {
            String match = matches.get(matchPointer);
            System.out.println("Transforming into a token the match: " + match); // For debugging.
            Token token;

            // Order of precedence:
            // Parentheses (done)
            // TODO: Exponents
            // TODO: Multiplication
            // TODO: Division
            // TODO: Addition
            // TODO: Subtraction

            if (match.equals("(")) {
                Matcher nestedMatcher = nestedEquationRegEx.matcher(equation);
                if (!nestedMatcher.find()) {
                    throw new RuntimeException("Paren error.");
                }
                String nestedEquation = nestedEquation(nestedMatcher.group());
                token = parseEquation(nestedEquation);
                matchPointer += movePointerAfterNestedEquation(matches);
            }

            else {
                token = new ValueToken(0.0f);
            }
            tokens.add(token);
        }

        return new ValueToken(0.0f); // temp
    }




    private static boolean isOperator(final String str){
        return isOperatorRegEx.matcher(str).matches();
    }
    private static boolean isNumeric(final String str){
        return isNumericRegEx.matcher(str).matches();
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
