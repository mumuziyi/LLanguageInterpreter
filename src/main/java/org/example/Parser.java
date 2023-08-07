package org.example;

import org.example.Structure.ListStructure;
import org.example.Uitils.TypeChecker;
import org.example.expr.*;
import org.example.stmt.*;
import org.example.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.example.TokenType.*;

public class Parser {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    private final List<Token> tokens;
    private int current = 0;

    private int ListLayer = 0;

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
//        if (match(LIST)) return listDeclaration();
        if (match(FUN)) return funDeclaration();
        else return statement();
    }

    //list(number) list;
    private Stmt listDeclaration(){

        ListStructure value = parseList();

        Token name = consume(IDENTIFIER, "Expect identifier after function declaration");
        consume(SEMICOLON,"Expect ';' after declare the List");
        return new ListStmt(name,value);
    }

    // list( list(number) )
    private ListStructure parseList(){
        consume(LEFT_PAREN, "Expect '(' after ListDecl");
        TokenType type = null;
        Object body = null;
        ListStructure innerList = null;

        /*
        var a = 3;
         */
        if (!check(RIGHT_PAREN)) {
            if (match(NUMBER)){
                type = NUMBER;
                body = new ArrayList<Double>();
            }else if (match(STRING)){
                type = STRING;
                body = new ArrayList<String>();
            }else if (match(BOOLEAN)){
                type = BOOLEAN;
                body = new ArrayList<Boolean>();
            }else if (match(LIST)){
                type = LIST;
                innerList = parseList();
                body = new ArrayList<ListStructure>();
            }else {
                handler.outputErrorInfo("Unknown token type in type decl", tokens.get(current).line);
            }
        }
        consume(RIGHT_PAREN, "Expect ')' after type decl");

        return new ListStructure(type,body,innerList);
    }

    //


    private Stmt funDeclaration(){
        Token name = consume(IDENTIFIER,"Expect identifier after function declaration");

        // Start scan the parameters
        consume(LEFT_PAREN, "Expect '(' after function identifier");
        List<Token> parameters = new ArrayList<>();
        List<Type> paramTypes = new ArrayList<>();
        while (!check(RIGHT_PAREN)){
            do {
                parameters.add(consume(IDENTIFIER,"Expect parameter name"));
                // if specify the type of the arguments
                if (match(COLON)){
                    Type curType = getType();
                    paramTypes.add(curType);
                }else {
                    paramTypes.add(new Type(Type.PrimitiveType.AnyType));
                }
            }while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' at the end of the function declaration");

        Type type = new Type(Type.PrimitiveType.AnyType);
        if (match(COLON)){
            type = getType();
        }

        // Start scan the body
        consume(LEFT_BRACE,"Expect '{' before the function body.");

        List<Stmt> body = block();

        return new FunDecl(name,new Function(name,parameters,body,paramTypes), type);
    }


    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, " Expected identifier after var declaration");

        Type type = new Type(Type.PrimitiveType.AnyType);
        // whether user specify the type of the variable.
        if (match(COLON)){
            type = getType();
        }

        Expr initializer = null;
        if (match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expected ';' after variable declaration");
        return new Var(name,initializer,type);
    }

    // type list(a)  = <unit, (a, list(a))>
    //var a : string;
// var a  : <unit,(any, a)> = tuple(3, b);
    // var a  : <number,string>  = 3 ; // or 'hello world'


    private Stmt statement(){
        if (match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return blockStatement();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(RETURN)) return returnStatement();


        return expressionStatement();
    }

    private Stmt returnStatement(){
        Token keyword = tokens.get(current - 1);
        Expr value = null;
        if (tokens.get(current).type != SEMICOLON){
            value = expression();
        }
        consume(SEMICOLON,"Expect a ';' after the return value.");
        return new Return(keyword,value);
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
        Expr expr = monad();

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

    //var result = monad.x.bind().bind();
    private Expr monad(){
        Expr expr;
        List<Object> list = new ArrayList<>();

        if (match(MONAD)){
            while (match(DOT)){
                list.add(assignment());
            }
            expr = new Monad(list);
            return expr;
        }
        expr = tupleOrList();

        return expr;
    }

    // var a = tuple(1, tuple(1,2))
    private Expr tupleOrList(){
        if (match(TUPLE)){
            consume(LEFT_PAREN,"Expect '(' after tuple");
            Expr left = assignment();
            consume(COMMA,"Expect ',' between two variables");
            Expr right = assignment();
            consume(RIGHT_PAREN,"Expect ')' at the end of tuple");
            return new Tuple(left,right);
        }
        //var a = list 2 ：1:[]; // list []
        if (match(LIST) || ListLayer != 0){
//            ListLayer ++;
//            Expr left = assignment();
//            consume(COLON, "Expect ':' between list decl");
//            Expr right = assignment();
//            ListLayer --;

            //var a = list []:1:2:3;
            consume(LEFT_BRACKET,"Expect '[' after list decl");
            consume(RIGHT_BRACKET, "Expect ']' after list decl");
            List<Expr> list = new ArrayList<>();
            while (match(COLON)){
                list.add(assignment());
            }

            return new ListExpr(list);
        }

        return or();
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
        // left( right(a) ); right(a);
        if (match(LEFT,RIGHT)){
            TokenType type = getPrevious().type;
            consume(LEFT_PAREN,"Expect '(' after expression ");
            Expr inner = assignment();
            consume(RIGHT_PAREN,"Expect ')' after expression ");
            return new GetTupleLR(type,inner);

        }
        if (match(SHARP)){
            if (match(INCLUDE)){
                //# include "Math.lib","Util.lib";
                List<String> libList = new ArrayList<>();
                do {
                    libList.add((String) getCurrent().literal);
                    current+=1;
                }while (match(COMMA));

                return new Include(libList);
            }
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

    private Token getCurrent(){
        return tokens.get(current);
    }

    private Type getType(){
        if (match(STRING)){
            return new Type(Type.PrimitiveType.StringType);
        }
        if (match(BOOLEAN)){
            return new Type(Type.PrimitiveType.BoolType);
        }
        if (match(NUMBER)){
            return new Type(Type.PrimitiveType.NumberType);
        }
        if (match(ANY)){
            return new Type(Type.PrimitiveType.AnyType);
        }
        if (match(UNIT)){
            return new Type(Type.PrimitiveType.UnitType);
        }
        if (match(FUN)){
//            return new Type(Type.PrimitiveType.FunctionType);
            // Last is the return type.
            List<Type> typeList = new ArrayList<>();
            while (!match(CONVERT)){
                do {
                    typeList.add(getType());
                }while (match(COMMA));
            }
            typeList.add(getType());
            Type returnTyp = new Type(Type.PrimitiveType.FunctionType,typeList);
            return returnTyp;
        }
        if (match(LESS)){
            List<Type> params = new ArrayList<>();
            while (!check(GREATER)){
                do {
                    params.add(getType());
                }while (match(COMMA));
            }
            consume(GREATER,"Expect '>' after sumType decl");
            return new Type(Type.PrimitiveType.SumType, params);
        }

        if (match(LEFT_PAREN)){
            List<Type> params = new ArrayList<>();
            params.add(getType());
            consume(COMMA,"Expect ',' between two variables");
            params.add(getType());
            consume(RIGHT_PAREN,"Expect ')' after product type.");
            return new Type(Type.PrimitiveType.ProductType,params);
        }

        handler.outputErrorInfo("Undefined types for variable.",tokens.get(current).line);
        return null;
    }

}
