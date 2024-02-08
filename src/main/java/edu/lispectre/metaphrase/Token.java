package edu.lispectre.metaphrase;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static ch.obermuhlner.math.big.BigDecimalMath.pow;

public abstract class Token {
    Token left;
    Token right;
    Operator operator;
    BigDecimal val;
    TokenType type;

    public abstract BigDecimal eval();

    public abstract void changeValue(BigDecimal val);

    public TokenType getType() {
        return this.type;
    }

    public enum TokenType {
        OPERATOR,
        VALUE,
        VARIABLE
    }
}

class OperatorToken extends Token {
    // TODO: make the below customizable by Tokenizer creating the token and the constructor for this
    private static final MathContext mc = new MathContext(100, RoundingMode.HALF_UP);

    OperatorToken(Operator op){
        this.operator = op;
        this.type = TokenType.OPERATOR;
    }
    OperatorToken(Operator op, Token left, Token right){
        this.operator = op;
        this.left = left;
        this.right = right;
        this.type = TokenType.OPERATOR;
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

class ValueToken extends Token {
    ValueToken(BigDecimal val) {
        this.val = val;
        this.type = TokenType.VALUE;
    }

    ValueToken(String val) {
        this.val = new BigDecimal(val);
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

class VariableToken extends Token {
    final String variableIdentifier;

    VariableToken(String variableIdentifier){
        this.variableIdentifier = variableIdentifier;
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
        this.val = new BigDecimal(val);
    }
    @Override
    public String toString(){
        return this.variableIdentifier;
    }
}