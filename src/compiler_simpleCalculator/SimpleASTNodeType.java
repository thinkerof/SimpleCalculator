package compiler_simpleCalculator;

import compiler.ASTNodeType;

public enum SimpleASTNodeType implements ASTNodeType {
    STARTING(""),
    INTDECLARERSTATE(""),
    ASSIGNMENTSTATE(""),
    EXPRESSIONSTATE(""),
    ASSIGNMENT("="),
    INTDECLARER("int"),
    NOT("~"),
    ID("id"),
    VALUE("value"),
    PLUS("+"),PLUS2("++"),
    MINUS("-"),MINUS2("--"),
    STAR("*"),SLASH("/"),MOD("%"),
    AND("&"),OR("|"),XOR("^");
    private String text;
    SimpleASTNodeType(String text){
        this.text=text;
    }
    public String getText(){
        return text;
    }
}
