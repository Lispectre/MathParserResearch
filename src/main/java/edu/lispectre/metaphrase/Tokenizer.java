package edu.lispectre.metaphrase;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Equation tokenizer. Holds the tokens and variable info for one equation at the time. The tokenizer can be initialized
 * with an equation ({@link #Tokenizer(String)}  Tokenizer}. The equation can be passed or changed with
 * {@link #tokenizeEquation(String)} method.
 */
public class Tokenizer {
    private static final Pattern tokenizingRegEx = Pattern.compile("\\d*\\.?\\d+|[a-zA-Z]+|[\\^+\\-*/]|\\(|\\)");
    private static final Pattern isNumericRegEx = Pattern.compile("\\d*\\.?\\d+");
    private static final Pattern nestedEquationRegEx = Pattern.compile("\\([^)]*\\)");
    private static final ArrayList<String> operators = new ArrayList<>(Arrays.asList("+", "(", "-", "*", "/", "^"));
    private final ArrayList<Token> unparsedTokens = new ArrayList<>();
    private final HashMap<String, Token> variableAccess = new HashMap<>();
    private MathContext mathcontext;


    Tokenizer() {
    }


    Tokenizer(String equation) {
        tokenizeEquation(equation);
    }

    Tokenizer(String equation, int desiredPrecision) {
        tokenizeEquation(equation, desiredPrecision);
    }

    /**
     * Method for extracting an equation in parentheses. This allows the code to recursively tokenize and parse the
     * inner equation instead of treating parentheses like tokens themselves.
     * @param matches symbols which are tokenized
     * @param index point in the list at which open parenthesis was detected
     * @return the equation inside parentheses
     */
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

    /**
     * Calculates the amount of symbols we "ignore", as they were already accounted for by
     * {@link #nestedEquation(ArrayList, int)}.
     *
     * @param matches symbols which are tokenized
     * @param index   point in the list at which open parenthesis was detected
     * @return amount of symbols to skip
     */
    private static int movePointerAfterNestedEquation(final ArrayList<String> matches, final int index) {
        int unmatchedParenthesis = 1;
        int i;

        for (i = index; unmatchedParenthesis > 0; i++) {
            String match = matches.get(i);
            if (match.equals("(")) {
                unmatchedParenthesis++;
            }
            if (match.equals(")")) {
                unmatchedParenthesis--;
            }
        }
        return i - index;
    }

    /**
     * Tokenizes the equation.
     * <p>The corresponding tokens are put in {@link #unparsedTokens}, which can be accessed using the {@link #getTokens()} method.
     * Beware, that when tokenizing an equation, tokens and variables of the previous one get erased.
     *
     * @param equation equation to create tokens of
     * @param DESIRED_PRECISION the amount of decimal places
     * @throws IllegalArgumentException if the DESIRED_PRECISION param is set to zero or lower
     */
    public void tokenizeEquation(String equation, final int DESIRED_PRECISION) {
        final Matcher matcher = tokenizingRegEx.matcher(equation);
        final Matcher nestedMatcher = nestedEquationRegEx.matcher(equation);
        final ArrayList<String> matches = new ArrayList<>();
        this.mathcontext = new MathContext(DESIRED_PRECISION);

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
                    variableAccess.putAll(nestedEquationTokenizer.getVariables());
                    token = Parser.parseTokens(nestedEquationTokens, this.mathcontext);
                    index += movePointerAfterNestedEquation(matches, index+1);
                    break;
                case ")":
                    throw new RuntimeException("Parentheses error - unmatched closing parenthesis.");
                case "^":
                    token = new OperatorToken(Operator.EXPONENT, this.mathcontext);
                    break;
                case "*":
                    token = new OperatorToken(Operator.MULTIPLICATION, this.mathcontext);
                    break;
                case "/":
                    token = new OperatorToken(Operator.DIVISION, this.mathcontext);
                    break;
                case "+":
                    token = new OperatorToken(Operator.ADDITION, this.mathcontext);
                    break;
                case "-":
                    if (index == 0 || operators.contains(matches.get(index - 1))) {
                        token = new OperatorToken(Operator.UNARYMINUS, this.mathcontext);
                    } else {
                        token = new OperatorToken(Operator.SUBTRACTION, this.mathcontext);
                    }
                    break;
                default:
                    if (isNumericRegEx.matcher(symbol).matches()){
                        token = new ValueToken(symbol, this.mathcontext);
                    }
                    else {
                        if (!variableAccess.containsKey(symbol)) {
                            token = new VariableToken(symbol, this.mathcontext);
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

    /**
     * Tokenizes the equation, making the tokens accurate up to a 10th decimal place.
     * <p>The corresponding tokens are put in {@link #unparsedTokens}, which can be accessed using the {@link #getTokens()} method.
     * Beware, that when tokenizing an equation, tokens and variables of the previous one get erased.
     *
     * @param equation equation to create tokens of
     */
    public void tokenizeEquation(String equation) {
        this.tokenizeEquation(equation, 10);
    }

    /**
     * Getter for {@code Token}s.
     * If at least one equation has been tokenized, this will return an {@code ArrayList} which can be passed to
     * Parser's {@code parseEquation} method.
     *
     * @return an {@code ArrayList} of {@code Token}s
     */
    public ArrayList<Token> getTokens() {
        return unparsedTokens;
    }

    /**
     * Returns a {@code HashMap} of {@code VariableToken}s.
     * The keys are their corresponding identifiers in the equation. This method shouldn't be used for changing values
     * of vars; it is better to use it as a way of retrieving identifiers and then using the {@link #changeValueOfVariable(String, BigDecimal)}
     * or {@link #changeValuesOfVariables(String[], BigDecimal[])} methods.
     *
     * @return a {@code HashMap} of variables that exist as tokens in this Tokenizer.
     */
    public HashMap<String, Token> getVariables() {
        return variableAccess;
    }

    /**
     * Returns a {@code MathContext}. Useful with the {@code parseTokens()} method.
     *
     * @return {@code MathContext} the Tokenizer used for creating tokens.
     */
    public MathContext getMathContext() {
        return mathcontext;
    }

    /**
     * Changes the value of a variable.
     * Accesses the {@code HashMap} of variables and calls {@code changeValue(double val)} with provided value on found variable.
     *
     * @param identifier the string of text by which the variable is declared in the equation
     * @param value      the value which the variable should be assigned
     * @throws NullPointerException if trying to change value of a non-existent variable
     */
    public void changeValueOfVariable(String identifier, BigDecimal value) {
        variableAccess.get(identifier).changeValue(value);
    }

    /**
     * Changes the value of multiple variables.
     * Calls {@link #changeValueOfVariable(String identifier, BigDecimal value)} with all the Strings and doubles in pairs sourced from the
     * arrays passed to this method. In essence this method is a wrapper that iterates over provided arrays.
     * This method doesn't need to change all variables in an equation.
     *
     * @param identifiers array of strings of text by which the variables are declared in the equation
     * @param values      values which the corresponding variables should be assigned
     * @throws NullPointerException   if trying to change value of a non-existent variable
     * @throws InputMismatchException if the length of params doesn't match
     */
    public void changeValuesOfVariables(String[] identifiers, BigDecimal[] values) {
        if (identifiers.length != values.length) {
            throw new InputMismatchException("The amount of variable identifiers and values does not match.");
        }
        for (int i = 0; i < identifiers.length; i++) {
            this.changeValueOfVariable(identifiers[i], values[i]);
        }
    }
}
