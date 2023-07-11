package org.example.Uitils;

import org.example.stmt.Return;

public class ReturnValue extends RuntimeException{
    public final Object value;

    public ReturnValue(Object value){
        super(null,null,false,false);
        this.value = value;
    }
}
