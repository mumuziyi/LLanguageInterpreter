package org.example;

public class ErrorAndExceptionHandler {

    public void outputErrorInfo(String errorInfo, int line){
        if (line == -1){
            System.err.println("Error: " + errorInfo);
        }else {
            System.err.println("[line " + line + " error: " + errorInfo);
        }
        System.exit(0);
    }
}
