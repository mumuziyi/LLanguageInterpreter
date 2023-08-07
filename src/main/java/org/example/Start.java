package org.example;

import org.example.Uitils.PrintAST;
import org.example.expr.*;
import org.example.stmt.Stmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Start {
    public static int line = 0;
    private static List<Token> tokenList = new LinkedList<>();
    public static boolean hadError = false;


    //        runFromTerminal();
//        String filePath = "src/main/resources/declarationTest";
//        String filePath = "src/main/resources/FlowControlTest";
//        public static String filePath = "src/main/resources/FunTest";
//        String filePath = "src/main/resources/DataStructureTest";

    // Week 6
//        String filePath = "src/main/resources/Types/ProductTypeTest";
//        String filePath = "src/main/resources/Types/SumTypeTest";

    // Week 7
//        public static String filePath = "src/main/resources/Types/AnyTypeTest";
//        public static String filePath = "src/main/resources/Types/4TupleGetTest";
//        public static String filePath = "src/main/resources/Types/5FunTest";
//    public static String filePath = "src/main/resources/Types/6LibTest";

    //Test

//    public static String filePath = "src/main/resources/Types/7TupleExprTest";
//    public static String filePath = "src/main/resources/Types/8ListTest";
//    public static String filePath = "src/main/resources/Types/9MonadTest";
//    public static String filePath =  "src/main/resources/Library/Util.Lib";
    public static void main(String[] args) {
        List<String> paths = new ArrayList<>();
        paths.add("src/main/resources/Library/Util.Lib");
        paths.add("src/main/resources/declarationTest");
        paths.add("src/main/resources/FlowControlTest");
        paths.add("src/main/resources/FunTest");
        paths.add("src/main/resources/Types/ProductTypeTest");
        paths.add("src/main/resources/Types/SumTypeTest");
        paths.add("src/main/resources/Types/AnyTypeTest");
        paths.add("src/main/resources/Types/4TupleGetTest");
        paths.add("src/main/resources/Types/5FunTest");
        paths.add("src/main/resources/Types/6LibTest");
        paths.add("src/main/resources/Types/7TupleExprTest");
        paths.add("src/main/resources/Types/8ListTest");
        paths.add("src/main/resources/Types/9MonadTest");

//        runFromFile(paths.get(9));


        for (String filepath: paths){

            System.out.println("File <" + filepath + "> is running");
            runFromFile(filepath);
            line = 0;
            tokenList = new ArrayList<>();
            hadError = false;
            System.out.println("Finish running \n");
        }

        System.out.println("Pass all the unit test");

    }

    // get code from terminal
    public static void runFromTerminal(){
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        System.out.println(code);

        getTokenList(code);

        line += 1;

    }

    // Get code form the file
    public static void runFromFile(String filePath){
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String code;
            while ((code = reader.readLine()) != null) {
                // 处理每一行的逻辑
                tokenList.addAll(getTokenList(code));

                line += 1;
            }
            tokenList.add(new Token(TokenType.EOF, "", null, line));
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Token> getTokenList(String code){
        Lexer lexer = new Lexer(code,line);
        return lexer.tokenize();
    }

    public static void run(){

        Parser parser = new Parser(tokenList);
        List<Stmt> expr = parser.parse();

//        PrintAST printer = new PrintAST();
//        System.out.println(printer.printAST(expr));

        Interpreter interpreter = new Interpreter();
        interpreter.interpreter(expr);

    }

}
