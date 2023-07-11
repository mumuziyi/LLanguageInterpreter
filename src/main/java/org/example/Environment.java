package org.example;

import java.util.HashMap;
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

    void assignVar(String name, Object value){
        if (values.containsKey(name)){
            values.put(name,value);
            return;
        }
        if (enclosing != null){
            enclosing.assignVar(name,value);
            return;
        }
        handler.outputErrorInfo("Can't assign a value to an undefined variable",-1);
    }

    Object getValue(Token name){
        if (values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
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

}
