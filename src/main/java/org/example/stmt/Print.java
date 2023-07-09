package org.example.stmt;

import org.example.expr.Expr;

public class Print extends Stmt{
    public Expr expr;

    public Print(Expr expr){
        this.expr = expr;
    }
}
