package org.example;

import org.example.expr.*;
import org.example.stmt.*;

import java.util.LinkedList;
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

    public List<Stmt> parse(){
        List<Stmt> statements = new LinkedList<>();
        while (!isAtEnd()){
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration(){
        if (match(VAR)) return varDeclaration();
        else return statement();
    }

    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, " Expected");

        Expr initializer = null;
        if (match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expected ';' after variable declaration");
        return new Var(name,initializer);
    }
    private Stmt statement(){
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return blockStatement();

        return expressionStatement();
    }

    private Stmt blockStatement(){
        return new Block(block());
    }

    private List<Stmt> block(){
        List<Stmt> blockStmts = new LinkedList<>();
        while (!match(RIGHT_BRACE) && !isAtEnd()){
            blockStmts.add(declaration());
        }
        current -= 1;
        consume(RIGHT_BRACE,"Expect '}' at the end of the block");
        return blockStmts;
    }

    private Stmt printStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after a value");
        return new Print(expr);
    }
    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression");
        return new Expression(expr);
    }


    private Expr expression(){
        return assignment();
    }

    private Expr assignment(){
        Expr expr = equality();

        if (match(EQUAL)){
            Token equals = tokens.get(current - 1);
            Expr value = assignment();

            if (expr instanceof Variable){
                Token name = ((Variable)expr).name;
                return new Assign(name,value);
            }

            handler.outputErrorInfo("Invalid assignment target",equals.line);
        }
        return expr;
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
        if (match(IDENTIFIER)){
            return new Variable(tokens.get(current - 1));
        }

        handler.outputErrorInfo("Unexpected token in primary()",tokens.get(current).line);
        return null;
    }

    private Token consume(TokenType type, String errorMsg){
        if (!match(type)){
            handler.outputErrorInfo(errorMsg, tokens.get(current).line);
            System.out.println("Error: " + errorMsg);
            System.exit(0);
        }
        return tokens.get(current - 1);
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
