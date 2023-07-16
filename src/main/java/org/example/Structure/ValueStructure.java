package org.example.Structure;

import org.example.type.Type;

// Variables structure in the environment. map<String name, Strut<Type type, Object value> >
public class ValueStructure {
    Type type;
    Object value;

    public ValueStructure(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
}
