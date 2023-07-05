package org.example.expr;

import org.example.Token;

public class Unary extends Expr{
    public Token operator;
    public Expr right;

    public Unary(Token operator, Expr right){
        this.operator = operator;
        this.right = right;
    }
}
