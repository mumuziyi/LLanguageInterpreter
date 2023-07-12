package org.example.Structure;

import org.example.TokenType;

public class ListStructure {
    public TokenType type;
    public Object value;
    ListStructure inner;

    public ListStructure(TokenType type, Object value,ListStructure inner) {
        this.type = type;
        this.value = value;
        this.inner = inner;
    }
}
