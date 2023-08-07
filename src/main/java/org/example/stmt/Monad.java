package org.example.stmt;

import org.example.expr.Expr;

import java.util.List;

public class Monad extends Expr {
    public List<Object> list;

    public Monad(List<Object> list) {
        this.list = list;
    }
}
