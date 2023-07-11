package org.example.stmt;

import org.example.Token;
import org.example.expr.Expr;

public class Return extends Stmt{
    public Token keyword;
    public Expr value;

    public Return(Token keyword, Expr value) {
        this.keyword = keyword;
        this.value = value;
    }
}
