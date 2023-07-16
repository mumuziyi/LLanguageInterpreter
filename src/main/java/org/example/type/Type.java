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
        NullType
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
        if (PrimitiveType.ProductType == pt || PrimitiveType.SumType == pt) {
            this.params.addAll(params);
        } else {
            //report error
        }
    }



}
