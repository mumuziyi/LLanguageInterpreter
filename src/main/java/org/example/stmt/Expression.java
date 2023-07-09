package org.example.stmt;

import org.example.expr.Expr;

public class Expression extends Stmt {
    public Expr expr;

    public Expression(Expr expr){
        this.expr = expr;
    }

}
