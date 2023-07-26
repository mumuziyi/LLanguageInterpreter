package org.example;

import org.example.Structure.ValueStructure;
import org.example.Uitils.ReturnValue;
import org.example.Uitils.TypeChecker;
import org.example.stmt.FunDecl;
import org.example.stmt.Stmt;
import org.example.type.Type;

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
        FunDecl decl = (FunDecl) funEnv.getFunction(name);

        Function function = decl.function;
        List<Token> parameter = function.params;
        List<Stmt> body = function.body;
        List<Type> types = function.paramTypes;

        if (parameter.size() != arguments.size()){
            handler.outputErrorInfo("Number of parameters and arguments doesn't match", -1);
            return null;
        }

        // bind the para and arguments
        for (int i = 0; i < parameter.size(); i++){
            Object value = arguments.get(i);
            Type required = types.get(i);
            Type given = TypeChecker.ObjectCheck(value);
            boolean isMeetRequirement = false;

            if (required.pt == Type.PrimitiveType.SumType){
                for (Type type : required.params){
                    if (given.equals(type)){
                        environment.addVar(parameter.get(i).lexeme, new ValueStructure(required,value));
                        isMeetRequirement = true;
                        break;
                    }
                }
            }else if (given.equals(required) || required.pt == Type.PrimitiveType.NullType){
                // delete last type (last is return type,shouldn't ad paramType)
                environment.addVar(parameter.get(i).lexeme,value);
                isMeetRequirement = true;
            }
            if (!isMeetRequirement){
                handler.outputErrorInfo("Input argument for function : '" + function.name.lexeme +"' didn't match the requirement",-1);
            }

        }
        Interpreter interpreter = new Interpreter(environment,funEnv);
        interpreter.interpreter(body);
        return null;
    }

}
