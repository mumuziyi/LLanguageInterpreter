package org.example.stmt;

import org.example.expr.Expr;

public class While extends Stmt{
    public Expr condition;
    public Stmt body;

    public While(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
    }
}
