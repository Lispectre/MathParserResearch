package edu.lispectre;

public abstract class Token {
    Token left;
    Token right;
    Operator operator;
    Float val;

    public abstract Float eval();
}

// TODO: Tokens for parentheses.

class OperatorToken extends Token {
    OperatorToken(Operator op, Token left, Token right){
        this.operator = op;
        this.left = left;
        this.right = right;
    }

    public Float eval() {
        switch(operator){
            case ADDITION -> this.val = left.eval() + right.eval();
            case SUBTRACTION -> this.val = left.eval() - right.eval();
            case DIVISION -> this.val = left.eval() / right.eval();
            case MULTIPLICATION -> this.val = left.eval() * right.eval();
        }

        return this.val;
    }
}

class ValueToken extends Token {
    ValueToken(Float val){
        this.val = val;
    }

    public Float eval() {
        return this.val;
    }
}

class VariableToken extends Token {
    String variableIdentifier;
    VariableToken(String variableIdentifier){
        this.variableIdentifier = variableIdentifier;
    }

    void changeVariable(Float val){
        this.val = val;
    }
    public Float eval() {
        return null;
    }
}