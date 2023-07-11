package org.example;

import org.example.stmt.Stmt;

import java.util.List;

public class FunctionExecution {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    Environment environment = new Environment();

    String name;
    List<Object> arguments;
    Environment funEnv;

    public FunctionExecution(String name, List<Object> arguments, Environment funEnv) {
        this.name = name;
        this.arguments = arguments;
        this.funEnv = funEnv;
    }

    // parameter for decl, argument for input value
    public Object call(){
        Function function = (Function) funEnv.getFunction(name);
        List<Token> parameter = function.params;
        List<Stmt> body = function.body;

        if (parameter.size() != arguments.size()){
            handler.outputErrorInfo("Number of parameters and arguments doesn't match", -1);
            return null;
        }

        // bind the para and arguments
        for (int i = 0; i < parameter.size(); i++){
            environment.addVar(parameter.get(i).lexeme,arguments.get(i));
        }

        Interpreter interpreter = new Interpreter(environment,funEnv);
        interpreter.interpreter(body);

        return null;
    }

}
