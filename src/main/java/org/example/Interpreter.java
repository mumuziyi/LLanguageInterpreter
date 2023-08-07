package org.example;

import org.example.Structure.TupleStructure;
import org.example.Structure.ValueStructure;
import org.example.Uitils.ReturnValue;
import org.example.Uitils.TypeChecker;
import org.example.expr.*;
import org.example.stmt.*;
import org.example.type.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.example.Start.getTokenList;
import static org.example.Start.line;

public class Interpreter {
    ErrorAndExceptionHandler handler = new ErrorAndExceptionHandler();
    Environment environment = new Environment();

    Environment funEnv = new Environment();

    // Only include statement use it;
    List<Stmt> ImportStmt;

    public Interpreter(Environment environment, Environment funEnv){
        this.environment = environment;
        this.funEnv = funEnv;
    }

    public Interpreter(){}


    // Get a list of statements from Parser, execute every statement one by one
    public void interpreter(List<Stmt> statements){
        ImportStmt = statements;
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
        funEnv.addVar(funDecl.name.lexeme,funDecl);
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
            Type given = TypeChecker.ObjectCheck(value);

            if (required.pt == Type.PrimitiveType.SumType){
                for (Type type : required.params){
                    if (given.equals(type)){
                        environment.addVar(var.name.lexeme, new ValueStructure(required,value));
                        return;
                    }
                }
                handler.outputErrorInfo("The type of '" + var.name.lexeme +"' isn't correct during decl", -1);
            }

            if (required.pt == Type.PrimitiveType.AnyType){
                environment.addVar(var.name.lexeme,new ValueStructure(required,value));
                return;
            }

            if (required.pt == Type.PrimitiveType.UnitType){
                environment.addVar(var.name.lexeme, new ValueStructure(required,value));
                return;
            }

            if (required.equals(given) || required.pt == Type.PrimitiveType.NullType){
                if (required.pt == Type.PrimitiveType.NullType){
                    var.type = given;
                }
                ValueStructure valueStructure = new ValueStructure(var.type,value);
                environment.addVar(var.name.lexeme,valueStructure);
            }else {
                handler.outputErrorInfo("The type of '" + var.name.lexeme +"' isn't correct during decl", -1);
            }
        }else {//initializer is null
            environment.addVar(var.name.lexeme,new ValueStructure(var.type,value));
        }
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
        if (expr instanceof GetTupleLR) return evaluateGetTupleLR(expr);
        if (expr instanceof Include) return evaluateInclude(expr);
        if (expr instanceof ListExpr) return evaluateListExpr(expr);
        if (expr instanceof Monad) return evaluateMonad(expr);
        return null;
    }

    private Object evaluateMonad(Expr expr){
        Monad monad = (Monad) expr;
        List<Object> list = monad.list;
        Object value = null;
        if (list.size() == 1){
            value = evaluate((Expr) list.get(0));
        }else if (list.size() == 0){
            handler.outputErrorInfo("Monad list is empty", -1);
        }else {
            value = evaluate((Expr) list.get(0));
            for (int i = 1; i < list.size(); i++){
                Call call = (Call) list.get(i);
                value = executeCall(call,value);
            }
        }

        return value;
    }



    private Object evaluateListExpr(Expr expr){
        ListExpr listExpr = (ListExpr) expr;
        List<Object> list = new ArrayList<>();
        for (Expr temp: listExpr.list){
            list.add(evaluate(temp));
        }
        return listExpr;
    }

    private Object evaluateInclude(Expr expr){
        String libPath = "src/main/resources/Library/";
        Include include = (Include)expr;
        List<String> importList = include.nameList;

        List<Stmt> MergedStmtList = ImportStmt;
        MergedStmtList.remove(0);
        List<Stmt> another = new ArrayList<>();

        for (String name: importList){
            another = getImportStmtList(libPath + name);
            another.addAll(MergedStmtList);
            MergedStmtList = another;
        }

        Interpreter interpreter = new Interpreter();
        interpreter.interpreter(another);
        System.exit(0);
        return null;
    }

    public List<Stmt> getImportStmtList(String filePath){
        List<Token> tokenList = new ArrayList<>();
        List<Stmt> expr = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String code;
            while ((code = reader.readLine()) != null) {
                // 处理每一行的逻辑
                tokenList.addAll(getTokenList(code));
            }
            tokenList.add(new Token(TokenType.EOF, "", null, -1));
            Parser parser = new Parser(tokenList);
            expr = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return expr;
    }
    private void executeIncludeExpr(Expr expr){
        Include importFileName = (Include)expr;
        List<String> libList = importFileName.nameList;
        String folderPath = "src/main/resources/Temp";
        String targetName = "MergeTemp";

        // If mergeTemp already exits, merge it with current lib
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader("src/main/resources/Library" + "/"
                    + importFileName.nameList.get(0)));
            BufferedReader reader2 = new BufferedReader(new FileReader(Start.filePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(folderPath + "/" + targetName));
            mergeFile(reader1,reader2,writer);

            for (int i = 1; i < libList.size(); i++){
                reader1 = new BufferedReader(new FileReader("src/main/resources/Library" + "/"
                        + importFileName.nameList.get(i)));
                reader2 = new BufferedReader(new FileReader(folderPath + "/" + targetName));
                mergeFile(reader1,reader2,writer);
            }
        }catch (Exception e){
            System.out.println(e);
        }


    }

    private void mergeFile(BufferedReader reader1, BufferedReader reader2, BufferedWriter writer){

        try {
            String line;

            // 合并第一个文件内容到输出文件
            while ((line = reader1.readLine()) != null) {
                writer.write(line);
                writer.newLine(); // 添加换行符
            }

            // 合并第二个文件内容到输出文件
            while ((line = reader2.readLine()) != null) {
                writer.write(line);
                writer.newLine(); // 添加换行符
            }

            // 关闭流
            reader1.close();
            reader2.close();
            writer.close();

        }catch (Exception e){
            System.out.println(e);
        }

    }


    // left( right(a) ) -> inner = right(a);
    private Object evaluateGetTupleLR(Expr expr){
        GetTupleLR getTupleLR = (GetTupleLR) expr;
        Expr inner = getTupleLR.inner;

        Object obj = evaluate(inner);

                // check whether the identifier is a variable.
        if (obj instanceof TupleStructure){
            TupleStructure tupleValue = (TupleStructure) obj;
            return getTupleLR.type == TokenType.LEFT?tupleValue.left : tupleValue.right;
        }else {
            handler.outputErrorInfo("The " + getTupleLR + " isn't a variable",-1);
        }

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

        return executeCall(call,null);


    }

    private Object executeCall(Call call, Object moreArguments){

        Object callee = evaluate(call.callee);
        FunDecl funDecl = (FunDecl)callee;
        Type requiredType = funDecl.returnType;

        Function function = funDecl.function;

        List<Object> arguments = new ArrayList<>();

        for (Expr argument : call.arguments){
            arguments.add(evaluate(argument));
        }
        // works for monad only
        if (moreArguments != null){
            arguments.add(moreArguments);
        }

        // prepare to execute
        FunctionExecution execution = new FunctionExecution(function.name.lexeme,arguments,funEnv);
        try {
            execution.call();
        }catch (ReturnValue returnValue){
            Type returnType = TypeChecker.ObjectCheck(returnValue.value);

            // If it's function type, don't check type until execute it.
            if (requiredType.pt == Type.PrimitiveType.FunctionType || requiredType.equals(returnType)){
//                funDecl.returnType = returnType;
//                funEnv.addVar(((Variable)call.callee).name.lexeme,funDecl);
                return returnValue.value;
            }else {
                handler.outputErrorInfo("Return type of the function <" + function.name.lexeme + "> didn't meet" +
                        "the requirement",-1);
            }


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
        Type type = TypeChecker.ObjectCheck(value);
        environment.assignVar(assign.name.lexeme,new ValueStructure(type,value));
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
