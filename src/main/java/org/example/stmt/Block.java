package org.example.stmt;

import java.util.List;

public class Block extends Stmt {
    public List<Stmt> stmts;

    public Block(List<Stmt> stmts){
        this.stmts = stmts;
    }
}
