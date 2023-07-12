package org.example.Uitils;

import org.example.*;

import java.util.List;

public class TypeChecker {

    Environment varEnv;
    Environment funEnv;


    public TypeChecker(Environment varEnv, Environment funEnv) {
        this.varEnv = varEnv;
        this.funEnv = funEnv;
    }


    public TokenType identifierCheck(String name){
        TokenType result;
        int count = 0;
        if (varEnv.getValueStr(name) != null){
            result = ObjectCheck(varEnv.getValueStr(name));
            count ++;
        }

        if (funEnv.getValueStr(name) != null){
            result = TokenType.FUN;
            count++;
        }
        if (count == 2){
            System.out.println("could be a number or a function, return NIL");
            return TokenType.NIL;
        }

        return TokenType.NIL;
    }

    public static TokenType ObjectCheck(Object object){
        if (object instanceof Integer || object instanceof Double || object instanceof Float){
            return TokenType.NUMBER;
        }
        if (object instanceof Boolean){
            return TokenType.BOOLEAN;
        }
        if (object instanceof String){
            return TokenType.STRING;
        }
        return null;
    }

    public static TokenType unaryCheck(Token operator, Object left){
        switch (operator.type){
            case MINUS:
                if (left instanceof Double){
                    return TokenType.NUMBER;
                }else return TokenType.NIL;
            case BANG:
                if (left instanceof Boolean){
                    return TokenType.BOOLEAN;
                }
        }
        return TokenType.NIL;
    }
    public static TokenType binaryCheck(Token operator, Object left, Object right){
        switch (operator.type){
            case MINUS:
            case SLASH:
            case STAR:
                if (left instanceof Double && right instanceof Double){
                    return TokenType.NUMBER;
                }else return TokenType.NIL;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return TokenType.NUMBER;
                }
                return TokenType.STRING;
            case GREATER_EQUAL:
            case GREATER:
            case LESS:
            case LESS_EQUAL:
                if ((left instanceof String && right instanceof String) ||
                        (left instanceof Double && right instanceof Double)){
                    return TokenType.BOOLEAN;
                }else {
                    return TokenType.NIL;
                }
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return TokenType.BOOLEAN;
        }
        return TokenType.NIL;
    }

    public static TokenType tokenType(Token token){
        return token.type;
    }
}
