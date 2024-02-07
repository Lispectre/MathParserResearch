package edu.lispectre;

public abstract class Token {
    Token left;
    Token right;
    Operator operator;
    Float val;
    TokenType type;
    public enum TokenType {
        OPERATOR,
        VALUE,
        VARIABLE
    }
    public abstract Float eval();
    public TokenType getType(){
        return this.type;
    }
}


class OperatorToken extends Token {

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

    public Float eval() {
        switch(operator){
            case EXPONENT -> this.val = (float) Math.pow(left.eval(), right.eval());
            case MULTIPLICATION -> this.val = left.eval() * right.eval();
            case DIVISION -> this.val = left.eval() / right.eval();
            case ADDITION -> this.val = left.eval() + right.eval();
            case SUBTRACTION -> this.val = left.eval() - right.eval();
        }
        return this.val;
    }


    @Override
    public String toString(){
        String[] operators = {"^", "*", "/", "+", "-"};
        return operators[this.operator.ordinal()];
    }


}

class ValueToken extends Token {
    ValueToken(Float val){
        this.val = val;
        this.type = TokenType.VALUE;
    }

    public Float eval() {
        return this.val;
    }

    @Override
    public String toString(){
        return Float.toString(this.val);
    }
}

class VariableToken extends Token {
    final String variableIdentifier;
    VariableToken(String variableIdentifier){
        this.variableIdentifier = variableIdentifier;
        this.type = TokenType.VARIABLE;
    }

    void changeVariable(Float val){
        this.val = val;
    }
    public Float eval() {
        return null;
    }

    @Override
    public String toString(){
        return this.variableIdentifier;
    }
}