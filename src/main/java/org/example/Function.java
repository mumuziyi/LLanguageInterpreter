package org.example;

import org.example.stmt.Stmt;
import org.example.type.Type;

import java.util.List;

public class Function {
    public Token name;
    public List<Token> params;
    public List<Stmt> body;
    public List<Type> paramTypes;

    public Function(Token name, List<Token> params, List<Stmt> body,List<Type> paramTypes) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.paramTypes = paramTypes;
    }
}
