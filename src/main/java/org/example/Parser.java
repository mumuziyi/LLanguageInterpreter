package org.example;

import org.example.expr.*;

import java.util.List;

import static org.example.Start.hadError;
import static org.example.TokenType.*;

public class Parser {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public Expr parse(){
        try {
            return expression();
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    private Expr expression(){
        return equality();
    }

    private Expr equality(){
        Expr expr = comparison();

        while (match(BANG_EQUAL,EQUAL_EQUAL)){
            Token operator = getPrevious();
            Expr right = comparison();
            expr = new Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr comparison(){
        Expr expr = term();

        while (match(GREATER,GREATER_EQUAL,LESS,LESS_EQUAL)){
            Token operator = getPrevious();
            Expr right = term();
            expr = new Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr term(){
        Expr expr = factor();
        while (match(MINUS,PLUS)){
            Token operator = getPrevious();
            Expr right = factor();
            expr = new Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr factor(){
        Expr expr = unary();
        while (match(SLASH,STAR)){
            Token operator = getPrevious();
            Expr right = unary();
            expr = new Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr unary(){
        if (match(BANG,MINUS)){
            Token operator = getPrevious();
            Expr right = unary();
            return new Unary(operator,right);
        }
        return primary();
    }

    private Expr primary(){
        if(match(TRUE)) return new Literal("true");
        if (match(FALSE)) return new Literal("false");
        if (match(NIL)) return new Literal(null);

        if (match(NUMBER,STRING)){
            return new Literal(getPrevious().literal);
        }
        if (match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN,"Expect ')' after expression ");
            return new Grouping(expr);
        }

        handler.outputErrorInfo("Unexpected token in primary()",tokens.get(current).line);
        return null;
    }

    private void consume(TokenType type, String errorMsg){
        if (!match(type)){
            handler.outputErrorInfo(errorMsg, tokens.get(current).line);
            System.out.println("Error: " + errorMsg);
            System.exit(0);
        }
    }

    // check if the current token match the input.
    private boolean match(TokenType... types){
        for (TokenType type : types){
            if (check(type)){
                current += 1;
                return true;
            }
        }
        return false;
    }

    private Token getPrevious(){
        return tokens.get(current - 1);
    }

    private boolean check(TokenType type){
        if (isAtEnd()) return false;
        return tokens.get(current).type == type;
    }

    private boolean isAtEnd(){
        return tokens.get(current).type == EOF;
    }

}
