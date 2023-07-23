package org.example.Structure;

import org.example.type.Type;

import java.util.List;

public class FunType {
    public List<Type> input;
    public List<Type> output;

    public FunType(List<Type> input, List<Type> output) {
        this.input = input;
        this.output = output;
    }
}
