package org.example.expr;

import org.example.Token;

public class Variable extends Expr{
    public Token name;

    public Variable(Token name){
        this.name = name;
    }
}
