package edu.lispectre.mathparser;

public abstract class Token {
    Token left;
    Token right;
    Operator operator;
    Double val;
    TokenType type;


    public abstract Double eval();

    public abstract void changeVariable(Double val);

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

    public Double eval() {
        switch(operator){
            case EXPONENT -> this.val = Math.pow(left.eval(), right.eval());
            case MULTIPLICATION -> this.val = left.eval() * right.eval();
            case DIVISION -> this.val = left.eval() / right.eval();
            case ADDITION -> this.val = left.eval() + right.eval();
            case SUBTRACTION -> this.val = left.eval() - right.eval();
        }
        return this.val;
    }

    @Override
    public void changeVariable(Double val) {
        throw new RuntimeException("Something went horribly wrong if you see this error.");
    }

    @Override
    public String toString(){
        String[] operators = {"U-", "^", "*", "/", "+", "-"};
        return operators[this.operator.ordinal()];
    }
}

class ValueToken extends Token {
    ValueToken(Double val) {
        this.val = val;
        this.type = TokenType.VALUE;
    }

    public Double eval() {
        return this.val;
    }

    @Override
    public void changeVariable(Double val) {
        throw new RuntimeException("Something went horribly wrong if you see this error.");
    }

    @Override
    public String toString(){
        return Double.toString(this.val);
    }
}

class VariableToken extends Token {
    final String variableIdentifier;

    VariableToken(String variableIdentifier){
        this.variableIdentifier = variableIdentifier;
        this.type = TokenType.VARIABLE;
    }


    public Double eval() {
        return this.val;
    }

    @Override
    public void changeVariable(Double val) {
        this.val = val;
    }

    @Override
    public String toString(){
        return this.variableIdentifier;
    }
}