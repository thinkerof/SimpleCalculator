package compiler_simpleCalculator;

import compiler.Token;
import compiler.TokenType;

public class SimpleToken implements Token {
    private SimpleTokenType type;
    private String text;

    public SimpleToken(SimpleTokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return tokenString();
    }
}
