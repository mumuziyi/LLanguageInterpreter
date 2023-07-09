package org.example;

import org.example.expr.*;
import org.example.stmt.Expression;
import org.example.stmt.Print;
import org.example.stmt.Stmt;

import java.util.List;

public class Interpreter {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();


    // Get a list of statements from Parser, execute every statement one by one
    public void interpreter(List<Stmt> statements){
        try {
            for (Stmt statement: statements){
                execute(statement);
            }
        }catch (Exception e){
            System.out.println("Interpreter Error: " + e);
        }
    }

    // Check the type of the statement and execute it.
    private void execute(Stmt statement){
        if (statement instanceof Print){
            executePrint(statement);
        }
        if (statement instanceof Expression){
            executeExpression(statement);
        }
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
        if (expr instanceof Binary){
            return evaluateBinary(expr);
        }else if (expr instanceof Unary){
            return evaluateUnary(expr);
        }else if (expr instanceof Literal){
            return evaluateLiteral(expr);
        }else if (expr instanceof Grouping){
            return evaluateGrouping(expr);
        }
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
                handler.outputErrorInfo("Number can't plus with string", binary.operator.line);
                break;
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
