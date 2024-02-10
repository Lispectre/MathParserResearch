package edu.lispectre.metaphrase;

import java.math.BigDecimal;
import java.math.MathContext;

import static ch.obermuhlner.math.big.BigDecimalMath.pow;

/**
 * An abstract class for the tokens of an equation.
 * Used to create tokens for operators ({@link OperatorToken}), variables ({@link VariableToken}) and
 * values ({@link ValueToken}).
 */
public abstract class Token {
    Token left;
    Token right;
    Operator operator;
    BigDecimal val;
    TokenType type;
    MathContext mc;

    /**
     * @return a {@code BigDecimal} with a value this {@code Token} will evaluate
     */
    public abstract BigDecimal eval();

    /**
     * Allows for changing a value in a Token. Currently, works only for {@link VariableToken}, using this on other
     * type of Token will return an error (a subject to change in the case it turns out favorable for a {@link ValueToken}
     * to not be immutable).
     *
     * @param val a value the VariableToken should be associated with
     */
    public abstract void changeValue(BigDecimal val);

    /**
     * @return the enum with an information what type is this Token
     */
    public TokenType getType() {
        return this.type;
    }

    public enum TokenType {
        OPERATOR,
        VALUE,
        VARIABLE
    }
}

/**
 * A token that performs calculations based on its operator and two operands next to it.
 * Currently, the {@link #eval()} function always performs a new calculation, which may
 * not be ideal for speed, however ensures the value will be correct in case a variable
 * changes its value.
 */
class OperatorToken extends Token {

    OperatorToken(Operator op, MathContext mc) {
        this.operator = op;
        this.type = TokenType.OPERATOR;
        this.mc = mc;
    }

    OperatorToken(Operator op, Token left, Token right, MathContext mc) {
        this.operator = op;
        this.left = left;
        this.right = right;
        this.type = TokenType.OPERATOR;
        this.mc = mc;
    }

    public BigDecimal eval() {
        switch(operator){
            case EXPONENT -> this.val = pow(left.eval(), right.eval(), mc);
            case MULTIPLICATION -> this.val = left.eval().multiply(right.eval(), mc);
            case DIVISION -> this.val = left.eval().divide(right.eval(), mc);
            case ADDITION -> this.val = left.eval().add(right.eval(), mc);
            case SUBTRACTION -> this.val = left.eval().subtract(right.eval(), mc);
        }
        return this.val;
    }

    @Override
    public void changeValue(BigDecimal val) {
        throw new RuntimeException("Something went horribly wrong if you see this error.");
    }
    @Override
    public String toString(){
        final String[] operators = {"U-", "^", "*", "/", "+", "-"};
        return operators[this.operator.ordinal()];
    }
}

/**
 * Token which is a constant value in the equation.
 */
class ValueToken extends Token {
    ValueToken(BigDecimal val) {
        this.val = val;
        this.type = TokenType.VALUE;
    }

    ValueToken(String val, MathContext mc) {
        this.mc = mc;
        this.val = new BigDecimal(val, mc);
        this.type = TokenType.VALUE;
    }

    public BigDecimal eval() {
        return this.val;
    }

    @Override
    public void changeValue(BigDecimal val) {
        throw new RuntimeException("Something went horribly wrong if you see this error.");
    }
    @Override
    public String toString(){
        return this.val.toString();
    }
}

/**
 * Token which represents a variable in the equation. There can be multiple tokens like this in one equation, however
 * the Tokenizer ensures with its methods they all adopt the same value whenever that value is changed. Thus, changing
 * the value of a VariableToken without those methods, if absolutely necessary, should be done with extra care in case
 * there is another variable with the same name in the equation.
 */
class VariableToken extends Token {
    final String variableIdentifier;

    VariableToken(String variableIdentifier, MathContext mc) {
        this.variableIdentifier = variableIdentifier;
        this.mc = mc;
        this.type = TokenType.VARIABLE;
    }


    public BigDecimal eval() {
        return this.val;
    }

    @Override
    public void changeValue(BigDecimal val) {
        this.val = val;
    }

    public void changeValue(String val) {
        this.val = new BigDecimal(val, mc);
    }
    @Override
    public String toString(){
        return this.variableIdentifier;
    }
}