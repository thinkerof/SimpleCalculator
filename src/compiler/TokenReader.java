package compiler;

import java.util.List;

public class TokenReader implements ITokenReader {
    private List<Token> tokens;
    int pos=0;

    public TokenReader(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Token peek() {
        return pos<tokens.size()?tokens.get(pos):null;
    }

    @Override
    public Token read() {
        return pos<tokens.size()?tokens.get(pos++):null;
    }

    @Override
    public void unRead() {
        pos--;
    }

    @Override
    public Token[] peek(int n) {
        int size=(pos+n)>=tokens.size()?tokens.size()-pos:n;
        Token[] result= new Token[size];
        for(int i=0;i<size;i++){
            result[i]=tokens.get(pos+i);
        }
        return result;
    }

    @Override
    public Token[] read(int n) {
        int size=(pos+n)>=tokens.size()?tokens.size()-pos:n;
        Token[] result= new Token[size];
        for(int i=0;i<size;i++){
            result[i]=read();
        }
        return result;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public void setPos(int pos) {
        if(pos>=tokens.size()){
            this.pos=tokens.size();
        }else if(pos<0){
            this.pos=0;
        }else{
            this.pos=pos;
        }
    }
}
