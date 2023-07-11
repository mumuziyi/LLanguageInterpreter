package org.example.expr;

import org.example.Token;

import java.util.List;

public class Call extends Expr{
    public Expr callee;
    public Token paren;
    public List<Expr> arguments;

    public Call(Expr callee, Token paren, List<Expr> arguments) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }
}
