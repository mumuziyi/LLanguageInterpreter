package org.example.Structure;

import org.example.Uitils.TypeChecker;
import org.example.type.Type;

public class TupleStructure {
    public Object left;
    public Object right;

    public TupleStructure(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("( ");
        if (left instanceof TupleStructure ){
            builder.append(((TupleStructure)left).toString()).append(" ");
        }else {
            builder.append(left.toString()).append(" ");
        }

        builder.append(", ");

        if (right instanceof TupleStructure ){
            builder.append(((TupleStructure)right).toString()).append(" ");
        }else {
            builder.append(right.toString()).append(" ");
        }
        builder.append(" ) ");

        return builder.toString();
    }
}
