package org.example;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    private final Map<String, Object> values = new HashMap<>();


    void addVar(String name, Object value){
        values.put(name, value);
    }

    Object getValue(Token name){
        if (values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        handler.outputErrorInfo("Undefined variables",name.line);
        return null;
    }

}
