package compiler_simpleCalculator;

import compiler.ITokenParser;
import compiler.ITokenReader;
import compiler.Token;
import compiler.TokenReader;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 词法解析
 * 支持的token包括下面几种
 * 1.运算符 + - * / % | & ~ ^
 * 2.赋值 = += -= *= /= %= |= &= ^= ++ --
 * 3.数字 123 1,2,3 1_2_3 这三种都支持，但不支持 1,2_3
 * 4.关键字 int,这里的int并不等同于java中的int，java中的int有取值范围，而这里的int没有
 * 5.标识符,非全数字即可认定为标识符
 * 6.其他 ( ) ;
 */
public class SimpleTokenParser implements ITokenParser {
    private String script;
    private StringBuilder text = new StringBuilder();
    private SimpleTokenType type;
    private List<Token> tokenList = new ArrayList<>();
    private SimpleDfaState state = null;
    private Character tmp = null; //用于处理数字中','和'_'的，保证数字中最多只有一种除数字外的字符
    private int number_prefix_state=0;//0 前一个是数字   1前一个是 ， 2 前一个是 _

    public void setScript(String script) {
        if (script.length() == 0) {
            return;
        }
        clear();
        this.script = script;
        try {
            parse();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void parse() throws Exception {
        CharArrayReader reader = new CharArrayReader(script.toCharArray());
        int i;
        initState((char) reader.read());
        while ((i = reader.read()) != -1) {
            char c = (char) i;
            if(state==null){
                initState(c);
                continue;
            }
            switch (state) {
                case ID:
                    if (isAlpha(c) || isDigest(c)) {
                        text.append(c);
                    } else {
                        initState(c);
                    }
                    break;
                case ID_INT1:
                    if (c == 'n') {
                        text.append(c);
                        state = SimpleDfaState.ID_INT2;
                    } else if (isAlpha(c) || isDigest(c)) {
                        text.append(c);
                        state = SimpleDfaState.ID;
                    } else {
                        initState(c);
                    }
                    break;
                case ID_INT2:
                    if (c == 't') {
                        text.append(c);
                        state = SimpleDfaState.ID_INT3;
                    } else if (isAlpha(c) || isDigest(c)) {
                        text.append(c);
                        state = SimpleDfaState.ID;
                    } else {
                        initState(c);
                    }
                    break;
                case ID_INT3:
                    if (c == ' ') {
                        type = SimpleTokenType.INT;
                        initState(c);
                    } else if (isAlpha(c) || isDigest(c)) {
                        text.append(c);
                        state = SimpleDfaState.ID;
                    } else {
                        initState(c);
                    }
                    break;
                case NUMBER:
                    if (c == ',' || c == '_') {
                        if (tmp == null) {
                            tmp = c;
                            number_prefix_state=c==','?1:2;
                        } else if (tmp != c) {// 1,2_3
                            throw new Exception("Error number style");
                        } else if ((c==','&&number_prefix_state==1)||(c=='_'&&number_prefix_state==2)) {//1,,2
                            throw new Exception("Error number style");
                        }
                    } else if (isAlpha(c)) {
                        number_prefix_state=0;
                        text.append(c);
                        type = SimpleTokenType.IDECLARE;
                        state = SimpleDfaState.ID;
                    } else if (isDigest(c)) {
                        number_prefix_state=0;
                        text.append(c);
                    } else {
                        if (number_prefix_state!=0) {
                            throw new Exception("Error number style");//123,或者123_
                        }
                        type = SimpleTokenType.NUMBER;
                        initState(c);
                    }
                    break;
                case AND_OR_XOR:
                    if (c == '=') {
                        text.append(c);
                        type = SimpleTokenType.ASSIGNMENT;
                        state = SimpleDfaState.ASSIGNMENT;
                    } else {
                        initState(c);
                    }
                    break;
                case PLUS_MINUS:
                    if (c == '=') {
                        text.append(c);
                        type = SimpleTokenType.ASSIGNMENT;
                        state = SimpleDfaState.ASSIGNMENT;
                    } else if(c=='+'&&text.toString().equals("+")){
                        text.append(c);
                        type=SimpleTokenType.PLUS2;
                        state=SimpleDfaState.PLUS2;
                    }else if(c=='-'&&text.toString().equals("-")){
                        text.append(c);
                        type=SimpleTokenType.MINUS2;
                        state=SimpleDfaState.MINUS2;
                    }else {
                        initState(c);
                    }
                    break;
                case STAR_SLASH_MOD:
                    if (c == '=') {
                        text.append(c);
                        type = SimpleTokenType.ASSIGNMENT;
                        state = SimpleDfaState.ASSIGNMENT;
                    } else {
                        initState(c);
                    }
                    break;
                case MINUS2:
                case PLUS2:
                case ASSIGNMENT:
                case LEFT_PARENTHESIS:
                case SEMICOLON:
                case RIGHT_PARENTHESIS:
                case NOT:
                    initState(c);
                    break;
                default:
            }
        }
        if(text.length()!=0){
            if (type == SimpleTokenType.NUMBER&&number_prefix_state!=0) {
                throw new Exception("Error number style");//123,或者123_
            }
            SimpleToken token = new SimpleToken(type, text.toString());
            tokenList.add(token);
        }

    }

    private void initState(char c) {
        if (text.length() != 0) {
            SimpleToken token = new SimpleToken(type, text.toString());
            tokenList.add(token);
            text = new StringBuilder();
            tmp=null;
        }
        SimpleDfaState result = null;
        if (isAlpha(c)) {
            type = SimpleTokenType.IDECLARE;
            if (c == 'i') {
                result = SimpleDfaState.ID_INT1;
            } else {
                result = SimpleDfaState.ID;
            }
        } else if (isDigest(c)) {
            type=SimpleTokenType.NUMBER;
            result = SimpleDfaState.NUMBER;
        } else if (c == '=') {
            type = SimpleTokenType.ASSIGNMENT;
            result = SimpleDfaState.ASSIGNMENT;
        } else if (c == '~') {
            type = SimpleTokenType.NOT;
            result = SimpleDfaState.NOT;
        } else if (c == '+' || c == '-') {
            type=SimpleTokenType.PLUS_MINUS;
            result = SimpleDfaState.PLUS_MINUS;
        } else if (c == '*' || c == '/' || c == '%') {
            type=SimpleTokenType.STAR_SLASH_MOD;
            result = SimpleDfaState.STAR_SLASH_MOD;
        } else if (c == '&' || c == '|' || c == '^') {
            type=SimpleTokenType.AND_OR_XOR;
            result = SimpleDfaState.AND_OR_XOR;
        } else if (c == '(') {
            type = SimpleTokenType.PARENTHESES;
            result = SimpleDfaState.LEFT_PARENTHESIS;
        } else if (c == ')') {
            type = SimpleTokenType.PARENTHESES;
            result = SimpleDfaState.RIGHT_PARENTHESIS;
        } else if (c == ';') {
            type = SimpleTokenType.SEMICOLON;
            result = SimpleDfaState.SEMICOLON;
        }
        if (result != null) {
            text.append(c);
        }
        this.state = result;
    }

    //检查是否是字母或者下划线
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isDigest(char c) {
        return c >= '0' && c <= '9';
    }

    private void clear() {
        script = null;
        text = new StringBuilder();
        type = null;
        tmp=null;
        number_prefix_state=0;
        tokenList.clear();
    }
    //for test
    public List<Token> list(){
        return tokenList;
    }

    @Override
    public ITokenReader getTokenReader() {
        return new TokenReader(tokenList);
    }
}
