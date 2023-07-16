# Introduction
try to learn something about interpreter. I'll create a new Language called L(Li)Language.<hr>  
You can use most of your coding experience in Java.(Actually, it's just because of laziness, currently I need to focus on   
understanding internal implementations, rather than how to create language. I will do that later).<hr>

But I made some simplifications. only two types of var: string and number(it can be int or double).  
You can check in TokenType.java.


# What it can do?
## Pipeline

- Lexer: Strings ====(Read char by char)====> List<Token>
- Parser: List<Token> ----(Recursive Descent)----> Statement
- Interpreter: Statements ----(Read expr recursively)----> Result
> Statement ::= normalExpr | printExpr  
> normalExpr ::= Binary | Unary | Grouping | Literal

> Example:
>
>new Expression.Binary(
>
> new Expression.Unary( new Token(TokenType.MINUS, "-",null,1), new Expression.Literal(123)),
>
>new Token(TokenType.STAR,"*",null,1),
>
> new Expression.Grouping(new Expression.Literal(45.67))
>
>);d



## Week2
1. it's just a lexer, transfer your code to tokens and identify them.
2. If there are some problem in your code, it will report the error and point out where you made these mistakes.<hr>
3. Can represent some expr:
>expr     → literal  
>| unary  
>| binary  
>| grouping ;
>
>literal        → NUMBER | STRING | "true" | "false" | "nil" ;
>
>grouping       → "(" expr ")" ;
>
>unary          → ( "-" | "!" ) expr ;
>
>binary         → expr operator expr ;
>
>operator       → "==" | "!=" | "<" | "<=" | ">" | ">="  
| "+"  | "-"  | "*" | "/" ;
## Week3
1. Recursive Descent parsing <hr>  
   precedence
> program -> declaration* EOF;
>
> declaration -> funDecl | varDecl | statement;
> 
> funDecl   -> "fun" function;
> 
> function -> IDENTIFIER "(" parameters? ")" block;
> 
> parameters -> IDENTIFIER ("," IDENTIFIER)*;
>
> varDecl       -> "var" IDENTIFIER (":" Type)?  ("=" expr )? ";";
>
> statement -> returnStmt | exprStmt | printStmt | block | ifStmt | whileStmt | forStmt;
> 
> returnStmt -> "return" expression? ";";
> 
> forStmt -> "for""(" (VarDecl | exprStmt | ";") expression? ";" expression? ")" statement;
> 
> whileStmt -> "while" "(" expression ")" statement;
>
> ifStmt -> "if" "(" expression ")" statement ("else" statement)?;
> 
> exprStmt -> expression ";";
>
> printStmt -> "print" expr ";"
> 
> block  -> "{" declaration "}";
>
>    expression   -> assignment;
> 
>    assignment   -> IDENTIFIER "=" assignment | logic_or;
> 
>    logic_or     -> logic_and("or" logic_and)*;
> 
>    logic_and    -> equality ("and" equality)*;
>
> equality       -> comparison ( ( "!=" | "==" ) comparison )* ;
>
> comparison     -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
>
> term           -> factor ( ( "-" | "+" ) factor )* ;
>
> factor         -> unary ( ( "/" | "\*" ) unary )* ;
>
> unary          -> ( "!" | "-" ) unary| call ;
> 
> call         -> primary("(" arguments? ")")*;
> 
> arguments    -> expression ("," arguments)*;
>
>primary        -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER;


> literal       -> NUMBER | STRING | "true" | "false" | "nil";
>
> grouping      -> "(" expr ")";
>
> unary         -> ("-" | "!" ) expr;
>
> binary        -> expr operator expr;
>
> operator      -> "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/" ;

## Week 4
1. Assignment syntax



associativity

![img_1.png](img_1.png)
2. interpreter，actually work for the simple function

# Difficulties
I think the XML may be more suitable for this project.
## week 2
1. So much error or exception need to be handled which makes my code extremely ugly and makes me crazy.
2. easy to think but difficult to implement.  
   such as '<'. i need to consider whether it is'<' or '<='? <hr>Every single feature is easy to implement, but there are many similar  
   but not identical tasks, so I often need to spend time thinking about whether I need a new function. And ensure that I   
   can clearly distinguish them in the future. At present, it is almost impossible T_T.


# Run program
Running Environment: JDK 1.8+;  
Main function in LLanguage. I put everything in the Main function for testing. pick function you want to run and re-annot them.  
two types:
1. read code from terminal(line by line)
2. read from the file(put the file in *Resource* directory), follow the instruction and input the filename;there is a   
   test file called **codeFile**. You can try it if you want.



# Exit
## from terminal
input nothing and press *Enter*.
## from File
execute all the code

# Error handle
**File** : Report error and finish the interpreter immediately. <hr>  
**Terminal**: Report error and ignore the current line, keep running.
1. Unexpected character.
2. Unterminated string. ...
3. more need to be done. But I don't want to spend too much time on it, so just forget about them for now.  
   Interpreter will never try to execute an error program (but it will keep scanning  
   till the end of the line)