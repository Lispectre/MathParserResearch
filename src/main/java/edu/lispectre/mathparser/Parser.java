package edu.lispectre.mathparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Parser {
    public static Token parseEquationTokens(final ArrayList<Token> naiveTokens){
        if (naiveTokens.isEmpty()) {
            throw new RuntimeException("Trying to parse an empty equation.");
        }
        // Base condition:
        if (naiveTokens.size() == 1){
            return naiveTokens.getFirst();
        }

        for (Precedence op : Precedence.values()){
            for (int i = 0; i < naiveTokens.size(); i++) {
                Token token = naiveTokens.get(i);
                if (token == null) {
                    continue;
                }
                if (token.getType() != Token.TokenType.OPERATOR){
                    continue;
                }
                if (token.left != null && token.right != null){
                    continue;
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
                        parsedToken = unaryMinus(naiveTokens, i);
                    } else {
                        ArrayList<Token> neighboringTokens = getAndRemoveClosest(naiveTokens, i);
                        parsedToken = new OperatorToken(token.operator, neighboringTokens.get(0), neighboringTokens.get(1));
                    }
                    naiveTokens.set(i, parsedToken);
                }
            }
        }

        naiveTokens.removeIf(Objects::isNull);
        return parseEquationTokens(naiveTokens);
    }

    private static Token unaryMinus(ArrayList<Token> list, int index) {
        Token token = new OperatorToken(Operator.MULTIPLICATION,
                new ValueToken(-1.0),
                list.get(index + 1));
        list.remove(index + 1);
        return token;
    }
    private static <T> ArrayList<T> getAndRemoveClosest(ArrayList<T> list, int index){
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
