package org.example.expr;

import java.util.List;

public class Include extends Expr{
    public List<String> nameList;

    public Include(List<String> nameList) {
        this.nameList = nameList;
    }
}
