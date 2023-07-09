package org.example.stmt;

import org.example.Token;
import org.example.expr.Expr;

public class Var extends Stmt{
    public Token name;
    public Expr initializer;

    public Var(Token name, Expr initializer){
        this.name = name;
        this.initializer = initializer;
    }

}
