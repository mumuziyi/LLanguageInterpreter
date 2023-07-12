package org.example.stmt;

import org.example.Structure.ListStructure;
import org.example.Token;
import org.example.TokenType;

public class ListStmt extends Stmt{
    public Token name;
    public ListStructure structure;

    public ListStmt(Token name, ListStructure structure) {
        this.name = name;
        this.structure = structure;
    }
}
