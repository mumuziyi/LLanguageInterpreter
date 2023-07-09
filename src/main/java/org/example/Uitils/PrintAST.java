package org.example.Uitils;

import org.example.Token;
import org.example.expr.*;

public class PrintAST {

    public String printAST(Expr expr){
        StringBuilder builder = new StringBuilder();
        if (expr instanceof Binary){
            builder.append(printBinary(expr));
        }else if (expr instanceof Grouping){
            builder.append(printGrouping(expr));
        }else if (expr instanceof Literal){
            builder.append(printLiteral(expr));
        }else if (expr instanceof Unary){
            builder.append(printUnary(expr));
        }
        return builder.toString();
    }

    private String printBinary(Expr expr){
        StringBuilder builder = new StringBuilder();

        Binary binary = (Binary) expr;
        Expr left = binary.left;
        Expr right = binary.right;
        Token operator = binary.operator;

        // 1 + 2 ---> (+ 1 2)
        builder.append("( ").append(operator.lexeme).append("  ").append(printAST(left)).append("  ")
                .append(printAST(right)).append(" )");

        return builder.toString();
    }

    private String printUnary(Expr expr){
        StringBuilder builder = new StringBuilder();
        Unary unary = (Unary) expr;
        builder.append("( ").append(unary.operator.lexeme).append("  ").append(printAST(unary.right));
        return builder.toString();
    }

    private String printLiteral(Expr expr){
        Literal literal = (Literal) expr;
        return literal.value.toString();
    }
    private String printGrouping(Expr expr){
        Grouping grouping = (Grouping) expr;
        StringBuilder builder = new StringBuilder();
        builder.append("( Grouping").append(printAST(grouping.expr)).append(" )");
        return builder.toString();
    }




}
