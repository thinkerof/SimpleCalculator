package compiler_simpleCalculator;

import compiler.TokenType;

public enum SimpleTokenType implements TokenType {
    IDECLARE("id"),
    NOT("~"),
    PARENTHESES("(",")"),
    SEMICOLON(";"),
    INT("int"),
    NUMBER("0-9"),
    ASSIGNMENT("=","+=","-=","*=","/=","%=","&=","|=","^="),
    PLUS_MINUS("+","-"),
    STAR_SLASH_MOD("*","|","%"),
    AND_OR_XOR("&","|","^"), // & | ^
    PLUS2("++"),
    MINUS2("--")
    ;
    private String[] types;
    private SimpleTokenType(String... types){
        this.types=types;
    }
    @Override
    public String[] getType() {
        return types;
    }


}
