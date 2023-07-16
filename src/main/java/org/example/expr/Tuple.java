package org.example.expr;

public class Tuple extends Expr{
    public Expr left;
    public Expr right;

    public Tuple(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }
}
