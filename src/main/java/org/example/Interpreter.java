package org.example;

import org.example.Structure.TupleStructure;
import org.example.Structure.ValueStructure;
import org.example.Uitils.ReturnValue;
import org.example.Uitils.TypeChecker;
import org.example.expr.*;
import org.example.stmt.*;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    Environment environment = new Environment();

    Environment funEnv = new Environment();

    public Interpreter(Environment environment, Environment funEnv){
        this.environment = environment;
        this.funEnv = funEnv;
    }

    public Interpreter(){}


    // Get a list of statements from Parser, execute every statement one by one
    public void interpreter(List<Stmt> statements){
        try {
            for (Stmt statement: statements){
                execute(statement);
            }
        }catch (Exception e){
            if (!(e instanceof ReturnValue)){
                System.out.println("Interpreter Error: " + e);
            }
            throw (ReturnValue) e;

        }
    }

    // Check the type of the statement and execute it.
    private void execute(Stmt statement){
        if (statement instanceof Print) executePrint(statement);

        if (statement instanceof Expression) executeExpression(statement);

        if (statement instanceof Var) executeVar(statement);

        if (statement instanceof Block) executeBlock(statement, new Environment(environment));

        if (statement instanceof If) executeIf(statement);

        if (statement instanceof While) executeWhile(statement);

        if (statement instanceof FunDecl) executeFunDecl(statement);

        if (statement instanceof Return) executeReturn(statement);

        if (statement instanceof ListStmt) executeList(statement);
    }

    private void executeList(Stmt statement){
        ListStmt listStmt = (ListStmt) statement;
        environment.addVar(listStmt.name.lexeme,listStmt.structure);
    }

    private void executeReturn(Stmt statement){
        Return returnStmt = (Return) statement;
        Object value = null;
        if (returnStmt.value != null){
            value = evaluate(returnStmt.value);
            throw new ReturnValue(value);
        }

    }

    private void executeFunDecl(Stmt stmt){
        FunDecl funDecl = (FunDecl) stmt;
        funEnv.addVar(funDecl.name.lexeme,funDecl.function);
    }
    private void executeWhile(Stmt statement){
        While whileStmt = (While) statement;
        while (isTruthy(evaluate(whileStmt.condition))){
            execute(whileStmt.body);
        }
    }

    private void executeIf(Stmt statement){
        If ifStmt = (If) statement;
        if (isTruthy(evaluate(ifStmt.condition))){
            execute(ifStmt.thenBranch);
        }else {
            execute(ifStmt.elseBranch);
        }
    }

    private void executeBlock(Stmt statement, Environment environment){
        Block block = (Block) statement;
        List<Stmt> blockStmts = block.stmts;

        Environment previous = this.environment;

        try {
            this.environment = environment;
            for (Stmt stmt : blockStmts){
                execute(stmt);
            }
        }catch (Exception e){
            System.out.println(e);
        }finally {
            this.environment = previous;
        }

    }

    private void executeVar(Stmt statement){
        Var var = (Var) statement;
        Object value = null;
        if (var.initializer != null){
            value = evaluate(var.initializer);
            Type required = var.type;
            if (!isTypeEqual(required,value)){
                handler.outputErrorInfo("Error: the type of the variable ", -1);
            }
        }


        ValueStructure valueStructure = new ValueStructure(var.type,value);

        environment.addVar(var.name.lexeme,valueStructure);
    }

    private boolean isTypeEqual(Type required, Object given){
        if (required.pt == Type.PrimitiveType.NullType){
            return true;
        }

        if (given instanceof TupleStructure){
            if (required.pt != Type.PrimitiveType.ProductType){
                return false;
            }
            TupleStructure tuple = (TupleStructure) given;

            Type givenLeft = TypeChecker.ObjectCheck(tuple.left);
            Type requireL = required.params.get(0);

            if (requireL.pt != givenLeft.pt){
                return false;
            }
            if (requireL.pt == Type.PrimitiveType.ProductType){
                if (!isTypeEqual(requireL,tuple.left)){
                    return false;
                }
            }

            Type givenRight = TypeChecker.ObjectCheck(tuple.right);
            Type requireR = required.params.get(1);
            if (requireR.pt != givenRight.pt){
                return false;
            }
            if (requireR.pt == Type.PrimitiveType.ProductType){
                if (!isTypeEqual(requireR,tuple.right)){
                    return false;
                }
            }
            return true;
        }else return TypeChecker.ObjectCheck(given).pt == required.pt;
    }





    // For the printStmt, just output the value of the expression behind the print.
    private void executePrint(Stmt statement){
        Print print = (Print) statement;
        Object value = evaluate(print.expr);
        System.out.println(stringfy(value));
    }

    //
    private void executeExpression(Stmt stmt){
        Expression expression = (Expression) stmt;
        evaluate(expression.expr);
    }


    // get the result of the expression.
    private Object evaluate(Expr expr){
        if (expr instanceof Binary)return evaluateBinary(expr);
        if (expr instanceof Unary)return evaluateUnary(expr);
        if (expr instanceof Literal)return evaluateLiteral(expr);
        if (expr instanceof Grouping)return evaluateGrouping(expr);
        if (expr instanceof Variable)return evaluateVariable(expr);
        if (expr instanceof Assign) return evaluateAssign(expr);
        if (expr instanceof Logical) return evaluateLogical(expr);
        if (expr instanceof Call) return evaluateCall(expr);
        if (expr instanceof Tuple) return evaluateTuple(expr);
        return null;
    }

    private Object evaluateTuple(Expr expr){
        Tuple tuple = (Tuple) expr;
        Object left = evaluate(tuple.left);
        Object right = evaluate(tuple.right);
        return new TupleStructure(left,right);
    }

    private Object evaluateCall(Expr expr){
        Call call = (Call) expr;

        Object callee = evaluate(call.callee);

        Function function = (Function) callee;

        List<Object> arguments = new ArrayList<>();

        for (Expr argument : call.arguments){
            arguments.add(evaluate(argument));
        }

        FunctionExecution execution = new FunctionExecution(function.name.lexeme,arguments,funEnv);
        try {
            execution.call();
        }catch (ReturnValue returnValue){
            return returnValue.value;
        }
        return null;

    }

    private Object evaluateLogical(Expr expr){
        Logical logical = (Logical) expr;
        Object left = evaluate(logical.left);
        Object right = evaluate(logical.right);

        if (logical.operator.type == TokenType.OR){
            if (isTruthy(left) || isTruthy(right)){
                return true;
            }else return false;
        }else {
            if (isTruthy(left) && isTruthy(right)){
                return true;
            }else return false;
        }
    }

    private Object evaluateAssign(Expr expr){
        Assign assign = (Assign) expr;
        Object value = evaluate(assign.value);

        environment.assignVar(assign.name.lexeme,value);
        return value;
    }

    private Object evaluateVariable(Expr expr){
        Variable variable = (Variable) expr;
        if (environment.getValue(variable.name) != null){
            return environment.getValue(variable.name);
        }
        if (funEnv.getValue(variable.name) != null){
            return funEnv.getValue(variable.name);
        }
        handler.outputErrorInfo("Undefined variables",variable.name.line);
        return null;
    }
    private Object evaluateGrouping(Expr expr){
        Grouping grouping = (Grouping) expr;
        return evaluate(grouping.expr);
    }

    private Object evaluateLiteral(Expr expr){
        Literal literal = (Literal) expr;
        return literal.value;
    }
    private Object evaluateUnary(Expr expr){
        Unary unary = (Unary)expr;
        Object right = evaluate(unary.right);

        switch (unary.operator.type){
            case MINUS:
                checkNumberOperand(unary.operator,right);
                return -(double)right;
            case BANG:
                return !isTruthy(right);
        }

        return null;
    }

    private Object evaluateBinary(Expr expr){
        Binary binary = (Binary) expr;
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);

        switch (binary.operator.type){
            case MINUS:
                checkNumberOperand(binary.operator,left,right);
                return (double)left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return (double)left + (double) right;
                }
                if (left instanceof String && right instanceof String){
                    return (String)left + (String) right;
                }
                return stringfy(left) + stringfy(right);
//                handler.outputErrorInfo("Number can't plus with string", binary.operator.line);
//                break;
            case STAR:
                checkNumberOperand(binary.operator,left,right);
                return (double)left * (double) right;
            case SLASH:
                checkNumberOperand(binary.operator,left,right);
                return (double)left / (double) right;
            case GREATER:
                checkNumberOperand(binary.operator,left,right);
                return (double)left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperand(binary.operator,left,right);
                return (double)left >= (double) right;
            case LESS:
                checkNumberOperand(binary.operator,left,right);
                return (double)left < (double) right;
            case LESS_EQUAL:
                checkNumberOperand(binary.operator,left,right);
                return (double)left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left,right);
            case EQUAL_EQUAL:
                return isEqual(left,right);
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand){
        if (operand instanceof Double) return;
        handler.outputErrorInfo("Operand must be a number.", operator.line);
    }

    private void checkNumberOperand(Token operator, Object left, Object right){
        if (left instanceof Double && right instanceof Double)return;
        handler.outputErrorInfo("Operand must be a number", operator.line);
    }


    private boolean isEqual(Object left, Object right){
        if (left == null && right == null){
            return true;
        }
        if (left == null) return false;

        return left.equals(right);
    }

    private String stringfy(Object object){
        if (object == null) return "nil";

        if (object instanceof Double){
            String text = object.toString();
            if (text.endsWith(".0")){
                text = text.substring(0,text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    // everything
    private boolean isTruthy(Object object){
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        if (object instanceof Double) return (double)object != 0;
        return true;
    }



}
