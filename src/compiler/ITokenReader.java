package compiler;

public interface ITokenReader {
    //预读token
    Token peek();
    //读取token
    Token read();
    //取消去读token
    void unRead();
    //预读多个Token
    Token[] peek(int n);
    //读取多个token
    Token[] read(int n);
    //获取位置
    int getPos();
    //设置位置
    void setPos(int pos);
}
