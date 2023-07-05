package org.example.expr;

public class Grouping extends Expr{
    public Expr expr;

    public Grouping(Expr expr){
        this.expr = expr;
    }
}
