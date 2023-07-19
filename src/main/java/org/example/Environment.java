package org.example;

import org.example.Structure.TupleStructure;
import org.example.Structure.ValueStructure;
import org.example.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();

    Environment enclosing;

    Environment(){
        enclosing = null;
    }

    Environment(Environment enclosing){
        this.enclosing = enclosing;
    }
    private final Map<String, Object> values = new HashMap<>();


    void addVar(String name, Object value){
        values.put(name, value);
    }

    void assignVar(String name, Object object){
        if (values.containsKey(name)){
            ValueStructure given = (ValueStructure) object;
            ValueStructure required = (ValueStructure) values.get(name);
            // If it's sumType, check in a loop
            if (required.type.pt == Type.PrimitiveType.SumType){
                for (Type type: required.type.params){
                    if (given.type.equals(type)){
                        values.put(name,new ValueStructure(required.type,given.value));
                        return;
                    }
                }
                handler.outputErrorInfo("The type of assign to '"+ name +"' didn't match required type", -1);
            }
            // If user didn't specify the type or they are the same type
            if (required.type.pt == Type.PrimitiveType.NullType ||given.type.equals(required.type)){
                values.put(name,new ValueStructure(given.type,given.value));
                return;
            }
            handler.outputErrorInfo("The type of assign to '"+ name +"' didn't match required type", -1);
        }
        if (enclosing != null){
            enclosing.assignVar(name,object);
            return;
        }
        handler.outputErrorInfo("Can't assign a value to an undefined variable",-1);
    }

    public Object getValue(Token name){
        if (values.containsKey(name.lexeme)){
            Object obj = values.get(name.lexeme);
            // If it's varEnv;
            if (obj instanceof ValueStructure){
                ValueStructure valueStructure = (ValueStructure) values.get(name.lexeme);
                return valueStructure.value;

            }else {// If it's fun env
                return values.get(name.lexeme);
            }
        }
        if (enclosing != null){
            return enclosing.getValue(name);
        }

//        handler.outputErrorInfo("Undefined variables",name.line);
        return null;
    }

    Object getFunction(String name){
        if (values.containsKey(name)){
            return values.get(name);
        }
        if (enclosing != null){
            return enclosing.getFunction(name);
        }
        handler.outputErrorInfo("Undefined variables",-1);
        return null;
    }

    public Object getValueStr(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        if (enclosing != null) {
            return enclosing.getValueStr(name);
        }
        return null;
    }

}
