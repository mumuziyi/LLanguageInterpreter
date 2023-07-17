package org.example.Uitils;

import org.example.*;
import org.example.Structure.TupleStructure;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class TypeChecker {

    Environment varEnv;
    Environment funEnv;


    public TypeChecker(Environment varEnv, Environment funEnv) {
        this.varEnv = varEnv;
        this.funEnv = funEnv;
    }


    public TokenType identifierCheck(String name){
        TokenType result;
        int count = 0;
        if (varEnv.getValueStr(name) != null){
//            result = ObjectCheck(varEnv.getValueStr(name));
            count ++;
        }

        if (funEnv.getValueStr(name) != null){
            result = TokenType.FUN;
            count++;
        }
        if (count == 2){
            System.out.println("could be a number or a function, return NIL");
            return TokenType.NIL;
        }

        return TokenType.NIL;
    }

    public static Type ObjectCheck(Object object){
        if (object instanceof Integer || object instanceof Double || object instanceof Float){
            return new Type(Type.PrimitiveType.NumberType);
        }
        if (object instanceof Boolean){
            return new Type(Type.PrimitiveType.BoolType);
        }
        if (object instanceof String){
            return new Type(Type.PrimitiveType.StringType);
        }if (object instanceof List){
            return new Type(Type.PrimitiveType.ListType);
        }
        if (object instanceof TupleStructure){
            TupleStructure tupleStructure = (TupleStructure) object;
            List<Type> list = new ArrayList<>();
            Object left = tupleStructure.left;
            list.add(ObjectCheck(left));
            Object right = tupleStructure.right;
            list.add(ObjectCheck(right));
            return new Type(Type.PrimitiveType.ProductType,list);
        }

        return new Type(Type.PrimitiveType.NullType);
    }

    public static TokenType unaryCheck(Token operator, Object left){
        switch (operator.type){
            case MINUS:
                if (left instanceof Double){
                    return TokenType.NUMBER;
                }else return TokenType.NIL;
            case BANG:
                if (left instanceof Boolean){
                    return TokenType.BOOLEAN;
                }
        }
        return TokenType.NIL;
    }
    public static TokenType binaryCheck(Token operator, Object left, Object right){
        switch (operator.type){
            case MINUS:
            case SLASH:
            case STAR:
                if (left instanceof Double && right instanceof Double){
                    return TokenType.NUMBER;
                }else return TokenType.NIL;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return TokenType.NUMBER;
                }
                return TokenType.STRING;
            case GREATER_EQUAL:
            case GREATER:
            case LESS:
            case LESS_EQUAL:
                if ((left instanceof String && right instanceof String) ||
                        (left instanceof Double && right instanceof Double)){
                    return TokenType.BOOLEAN;
                }else {
                    return TokenType.NIL;
                }
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return TokenType.BOOLEAN;
        }
        return TokenType.NIL;
    }

    public static TokenType tokenType(Token token){
        return token.type;
    }
}
