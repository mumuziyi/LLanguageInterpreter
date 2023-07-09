package org.example.expr;

import org.example.Token;

public class Assign extends Expr {
    public Token name;
    public Expr value;

    public Assign(Token name, Expr expr){
        this.name = name;
        this.value = expr;
    }
}
