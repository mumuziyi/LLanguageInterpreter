package org.example.Uitils;

import org.example.Interpreter;
import org.example.Start;
import org.example.Token;
import org.example.TokenType;
import org.example.expr.Expr;

public class TypeChecker {

    public static TokenType cExprType(Expr expr){

        Interpreter interpreter = new Interpreter();
        Object object = interpreter.interpreter(expr);
        if (object == null) return TokenType.NIL;
        if (object instanceof Double) return TokenType.NUMBER;
        if (object instanceof String) return TokenType.STRING;
        if (object instanceof Boolean) return TokenType.BOOLEAN;

        return TokenType.NIL;
    }

    public static TokenType cTokenType(Token token){
        return token.type;
    }
}
