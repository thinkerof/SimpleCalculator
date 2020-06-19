package compiler;

public interface Token {

    String getText();

    TokenType getType() ;

    default String tokenString(){
        return "TokenType:"+getType()+",text:"+getText();
    }
}
