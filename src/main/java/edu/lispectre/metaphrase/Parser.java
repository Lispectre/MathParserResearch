package edu.lispectre.metaphrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Parser {
    public static Token parseTokens(final ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Trying to parse an empty equation.");
        }
        // Base condition:
        if (tokens.size() == 1) {
            return tokens.getFirst();
        }

        for (Precedence op : Precedence.values()){
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token == null) {
                    continue;                                       // Token was consumed as operand.
                }
                if (token.getType() != Token.TokenType.OPERATOR){
                    continue;                                       // Token is a variable or a value.
                }
                if (token.left != null && token.right != null){
                    continue;                                       // Token already has operands.
                }
                Precedence tokenPrecedence = switch (token.operator) {
                    case UNARYMINUS -> Precedence.UNARY;
                    case EXPONENT -> Precedence.EXPONENT;
                    case MULTIPLICATION, DIVISION -> Precedence.MULDIV;
                    case ADDITION, SUBTRACTION -> Precedence.ADDSUB;
                };
                if (tokenPrecedence == op) {
                    Token parsedToken;
                    if (tokenPrecedence == Precedence.UNARY) {
                        /* This means the next token regardless of its type or value will become an operand
                         * in a multiplication, along with a -1.0 value token. */
                        parsedToken = unaryMinus(tokens, i);
                    } else {
                        // This means the rightmost and leftmost tokens are this token's operands.
                        ArrayList<Token> operands = getAndConsumeOperands(tokens, i);
                        parsedToken = new OperatorToken(token.operator,
                                operands.get(0),
                                operands.get(1));
                    }
                    tokens.set(i, parsedToken);
                }
            }
        }
        tokens.removeIf(Objects::isNull);
        return parseTokens(tokens);
    }

    private static Token unaryMinus(ArrayList<Token> list, int index) {
        Token token = new OperatorToken(Operator.MULTIPLICATION,
                new ValueToken(-1.0),
                list.get(index + 1));
        list.remove(index + 1);
        return token;
    }

    private static <T> ArrayList<T> getAndConsumeOperands(ArrayList<T> list, int index) {
        ArrayList<T> pairClosest = new ArrayList<>(Collections.nCopies(2, null));

        for (int i = index-1; i>=0; i--){
            T closestLeft = list.get(i);
            if (closestLeft != null){
                pairClosest.set(0, closestLeft);
                list.set(i, null);
                break;
            }
        }
        for (int i = index+1; i<list.size(); i++){
            T closestRight = list.get(i);
            if (closestRight != null){
                pairClosest.set(1, closestRight);
                list.set(i, null);
                break;
            }
        }
        return pairClosest;
    }

    private enum Precedence{
        UNARY,
        EXPONENT,
        MULDIV,
        ADDSUB
    }

}
