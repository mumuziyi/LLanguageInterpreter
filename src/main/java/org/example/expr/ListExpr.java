package org.example.expr;


import java.util.ArrayList;
import java.util.List;

public class ListExpr extends Expr{

    public List<Expr> list = new ArrayList<>();
    public ListExpr(List<Expr> list) {
        this.list = list;
    }



}
