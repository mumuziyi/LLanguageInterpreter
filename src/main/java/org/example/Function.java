package org.example;

import org.example.stmt.Stmt;

import java.util.List;

public class Function {
    public Token name;
    public List<Token> params;
    List<Stmt> body;

    public Function(Token name, List<Token> params, List<Stmt> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
}
