package org.example.stmt;

import org.example.expr.Expr;

public class If extends Stmt{
    public Expr condition;
    public Stmt thenBranch;
    public Stmt elseBranch;

    public If(Expr condition, Stmt thenBranch, Stmt elseBranch){
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
