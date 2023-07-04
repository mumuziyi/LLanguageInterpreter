package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Start {
    public static int line = 0;
    private static List<Token> tokenList = new LinkedList<>();
    public static boolean hadError = false;
    public static void main(String[] args) {
//        runFromTerminal();

        runFromFile();

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
    public static void runFromFile(){
        String filePath = "src/main/resources/CodeFile1"; // 替换为实际文件的路径


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

        for (int i = 0; i < tokenList.size(); i++){
            System.out.println(tokenList.get(i).toString());
        }
    }

}
