package org.example.stmt;

import org.example.Token;
import org.example.expr.Expr;
import org.example.type.Type;

import java.util.List;

public class Var extends Stmt{
    public Token name;
    public Expr initializer;
    public Type type;

    public Var(Token name, Expr initializer, Type type){
        this.name = name;
        this.initializer = initializer;
        this.type = type;
    }

}
