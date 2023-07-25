package org.example.stmt;

import org.example.Function;
import org.example.Token;
import org.example.type.Type;

import java.util.List;

public class FunDecl extends Stmt{
    public Token name;

    public Function function;

    // last para is return type;
    public Type returnType;

    public FunDecl(Token name, Function function, Type returnType) {
        this.name = name;
        this.function = function;
        this.returnType = returnType;
    }
}
