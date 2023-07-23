package org.example.expr;

import org.example.Token;
import org.example.TokenType;

public class GetTupleLR extends Expr{

    public TokenType type;
    public Expr inner;


    public GetTupleLR(TokenType type, Expr inner) {
        this.type = type;
        this.inner = inner;
    }
}
