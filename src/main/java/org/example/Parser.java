package org.example;

import org.example.expr.*;
import org.example.stmt.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();

        return expressionStatement();
    }

    private Stmt forStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'for'");

        Stmt initializer;
        if (match(SEMICOLON)){
            initializer = null;
        }else if (match(VAR)){
            initializer = varDeclaration();
        }else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)){
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition");

        Expr increment = null;
        if (!check(SEMICOLON)){
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after loop increment");

        Stmt body = statement();

        if (increment != null){
            body = new Block(Arrays.asList(body,new Expression(increment)));
        }

        if (condition == null){
            condition = new Literal(true);
        }

        body = new While(condition,body);

        if (initializer != null){
            body = new Block(Arrays.asList(initializer,body));
        }

        return body;
    }

    private Stmt whileStatement(){
        consume(LEFT_PAREN, "Expected '(' after the 'while'");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected '(' after the while-condition");

        Stmt body = statement();
        return new While(condition,body);
    }
    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'if' ");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if-condition ");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)){
            elseBranch = statement();
        }

        return new If(condition,thenBranch,elseBranch);
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
        Expr expr = or();

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

    private Expr or(){
        Expr expr = and();

        while (match(OR)){
            Token operator = tokens.get(current - 1);
            Expr right = and();
            expr = new Logical(expr,operator,right);
        }
        return expr;
    }

    private Expr and(){
        Expr expr = equality();

        while (match(AND)){
            Token operator = tokens.get(current - 1);
            Expr right = equality();
            expr = new Logical(expr,operator,right);
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
        return call();
    }

    // if find (, start evaluate the arguments. it can allow fun()();
    private Expr call(){
        Expr expr = primary();

        while (true){
            if (match(LEFT_PAREN)){
                expr = finishCall(expr);
            }else break;
        }
        return expr;
    }

    private Expr finishCall(Expr callee){
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)){
            do {
                arguments.add(expression());
            }while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments ");

        return new Call(callee,paren,arguments);
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
