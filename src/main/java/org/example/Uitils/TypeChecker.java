package org.example.Uitils;

import org.example.Token;
import org.example.TokenType;
import org.example.expr.Expr;

public class TypeChecker {
    public static TokenType cExprType(Expr expr){
        return null;
    }

    public static TokenType cTokenType(Token token){
        return token.type;
    }
}
