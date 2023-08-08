package org.example.stmt;

import org.example.expr.Expr;

import java.util.List;

public class Bind extends Expr {
    public List<Object> list;

    public Bind(List<Object> list) {
        this.list = list;
    }
}
