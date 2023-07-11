package org.example.stmt;

import org.example.Function;
import org.example.Token;

import java.util.List;

public class FunDecl extends Stmt{
    public Token name;

    public Function function;

    public FunDecl(Token name, Function function) {
        this.name = name;
        this.function = function;
    }
}
