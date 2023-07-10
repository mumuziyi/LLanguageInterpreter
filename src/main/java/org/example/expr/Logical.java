package org.example.expr;

import org.example.Token;

public class Logical extends Expr{
    public Expr left;
    public Token operator;
    public Expr right;

    public Logical(Expr left, Token operator, Expr right){
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}
