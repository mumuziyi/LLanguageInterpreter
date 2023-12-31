package org.example.type;

import java.util.ArrayList;
import java.util.List;

public class Type {

    public PrimitiveType pt =  null;
    public List<Type> params  = new ArrayList<Type>();
    public enum PrimitiveType {
        UnitType,
        AnyType,
        StringType,
        BoolType,
        NumberType,
        ListType,
        ProductType,
        SumType,
        NullType,
        FunctionType
    }
    public Type(PrimitiveType pt) {
       this.pt  = pt;
    }

//    List( list (number ) );
    // var a : ()  = ()
    public Type(PrimitiveType pt, Type param) {
        this.pt = pt;
        if (PrimitiveType.ListType  == pt) {
            this.params.add(param);
        } else {
            //report error
        }
    }
    // type list(a)  = <unit, (a, list(a))>
    // var a  : <unit,(any, a)> = tuple(3, b);
    // var a  : <number,string>  = 3 ; // or 'hello world'
    public Type(PrimitiveType pt, List<Type> params) {
        this.pt = pt;
        if (PrimitiveType.ProductType == pt || PrimitiveType.SumType == pt|| pt == PrimitiveType.FunctionType) {
            this.params.addAll(params);
        } else {
            //report error
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Type other = (Type) obj;

        if (pt == PrimitiveType.AnyType || ((Type) obj).pt == PrimitiveType.AnyType){
            return true;
        }

        // 检查 pt 是否相等
        if (pt == null) {
            if (other.pt != null) {
                return false;
            }
        } else if (!pt.equals(other.pt)) {
            return false;
        }

        // 检查 params 中的值是否相等
        if (params == null) {
            if (other.params != null) {
                return false;
            }
        } else if (!params.equals(other.params)) {
            return false;
        }

        return true;
    }


}
