package org.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.example.TokenType.*;
public class Lexer {
    private String code;
    private int line;

    // Change every scan();
    private int current = 0;

    // start index for the token. change if start scan next java.Token
    private int start = 0;

    private static final Map<String, TokenType> keywords;static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun", FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
        keywords.put("number",  NUMBER);
        keywords.put("string",  STRING);
        keywords.put("list", LIST);
        keywords.put("tuple",TUPLE);
        keywords.put("any",ANY);
        keywords.put("unit",UNIT);
        keywords.put("left", LEFT);
        keywords.put("right", RIGHT);
        keywords.put("include",INCLUDE);
        keywords.put("funtype",FUNTYPE);
        keywords.put("monad",MONAD);
    }

    List<Token> tokenList = new LinkedList<>();

    ErrorAndExceptionHandler errorAndExceptionHandler = new ErrorAndExceptionHandler();

    Lexer(String code, int line){
        this.code = code;
        this.line = line;
    }

    public List<Token> tokenize(){
        // Read char by char
        while (!atEnd()){
            start = current;
            scan();
        }

        return tokenList;
    }

    public void scan(){
        char cur = getCurAndMove();
        switch (cur){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-':
                addToken(nextMatch('>')? CONVERT:MINUS );break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '#': addToken(SHARP); break;
//            case '[': addToken(nextMatch(']')?LR_BRACKET: LEFT_BRACKET); break;
            case ']': addToken(RIGHT_BRACKET);break;
            case '[': addToken(LEFT_BRACKET);break;
            case ':': addToken(nextMatch(':')? COLON_COLON: COLON); break;
                // check is ! or !=
            case '!': addToken(nextMatch('=')? BANG_EQUAL: BANG);break;
            case '=': addToken(nextMatch('=')? EQUAL_EQUAL: EQUAL);break;
            case '<': addToken(nextMatch('=')? LESS_EQUAL: LESS);break;
            case '>': addToken(nextMatch('=')? GREATER_EQUAL: GREATER);break;
            case '/':
                if (nextMatch('/')){
                    current = code.length();
                }else {
                    addToken(SLASH);
                }break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(cur)){
                    number();
                    break;
                }
                if (isChar(cur)){
                    identifier();
                    break;
                }
                errorAndExceptionHandler.outputErrorInfo("Illegal token.",line);
                Start.hadError = true;

        }

    }

    private void identifier(){
        while (isCharOrDigit(peek())){
            current ++;
        }
        String text = code.substring(start,current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isCharOrDigit(char c){
        return isChar(c) || isDigit(c);
    }

    private void string(){
        while (peek() != '"'){
            current++;
            if (atEnd()){
                errorAndExceptionHandler.outputErrorInfo("expect \" at the end of the string.", line);
                Start.hadError = true;
                break;
            }
        }
        if (!Start.hadError){
            current++;
            addToken(STRING,code.substring(start+ 1, current - 1));
        }

    }

    private boolean isChar(char c){
        return (c >= 'a' && c <= 'z') || (c >'A' && c < 'Z');
    }
    private void number(){
        if (atEnd()){
            addToken(NUMBER, Double.parseDouble(code.substring(start,current)));
            return;
        }


        if (peek() == '.'){
            getCurAndMove();
            if (atEnd()){
                errorAndExceptionHandler.outputErrorInfo("number ends with .",line);
            }
            while (!atEnd() && isDigit(peek())){
                current++;
            }
        }

        while (isDigit(peek())){
            current++;
        }

        if (!Start.hadError){
            addToken(NUMBER, Double.parseDouble(code.substring(start,current)));
        }


    }

    public boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    public boolean nextMatch(char expected){
        if (atEnd()){
            return false;
        }
        if (getCurAndMove() == expected){
            return true;
        }else {
            current -= 1;
            return false;
        }
    }

    public void addToken(TokenType type){
        tokenList.add(new Token(type,code.substring(start,current), null,line));
    }

    public void addToken(TokenType type, Object literal){
        tokenList.add(new Token(type, code.substring(start,current),literal,line));
    }

    public char peek(){
        return code.charAt(current);
    }
    private char getCurAndMove(){

        return code.charAt(current++);
    }

    private char getPrevious(){
        return code.charAt(current - 1);
    }

    private char getNext(){
        return code.charAt(current + 1);
    }

    private boolean atEnd(){
        if (current > code.length() - 1){
            return true;
        }
        return false;
    }
}
