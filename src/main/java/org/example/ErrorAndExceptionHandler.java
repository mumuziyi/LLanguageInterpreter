package org.example;

public class ErrorAndExceptionHandler {

    public void outputErrorInfo(String errorInfo, int line){
        System.err.println("[line " + line + " error: " + errorInfo);
    }
}
