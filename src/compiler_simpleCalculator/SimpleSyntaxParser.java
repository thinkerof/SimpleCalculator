package compiler_simpleCalculator;

import compiler.ASTNode;
import compiler.ITokenReader;
import compiler.Token;
import excep.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static compiler_simpleCalculator.SimpleASTNodeType.*;

/**
 * 语法规则
 * statement ::= intDeclarer|assignment|expression
 * intDeclare ::= 'int' id (';'|=expression)
 * assignment ::= id assign expression
 * expression ::= plus ;
 * plus ::= [('+-')*('+')? | ('-+')*('-')?]? andor ([('+-')*('+')? | ('-+')*('-')? ] andor)*
 * andor ::= mul(and mul)*
 * mul ::= not(star not)*
 * not ::= (~)* primary
 * primary ::= id|number|(plus)|id add2|add2 id
 * add ::= '+'|'-'
 * add2 ::= '++'|'--'
 * add ::= '&' | '|' | '^'
 * star ::= '*' | '/' | '%'
 * assign ::= '=' |'+=' | '-='| '*=' | '/=' | '%='| '&='| '|=' | '^='
 * <p>
 * intDeclare
 * 1. 未初始化，AST(root ::= 'int',index1 ::= AST(root::='=',index1::=id))
 * 2. 完成初始化 AST(root ::= 'int',index1 ::= AST(root::='=',index1::=id,index2::=value))
 * assignment
 * 1.AST(root::='=',index1::=id,index2::=value) //a=b
 * 2.AST(root::='=',index1::=id,index2::=AST(root::='+',index1::=id,index2::=value)) //a+=b
 * expression
 * 1.AST(root::='+',index1::=id,index2::=value) //可以是'+','-','*','/','%','^','|','&'
 * 2.AST(root::='~',index1::=id) //取反
 * 3.AST(root::='++',index1::=id) //id++
 * 4.AST(root::='=',index1::=id,index2::=AST(root::='+',child1=id,child2=1)) //++id
 * </p>
 */
public class SimpleSyntaxParser {
    private ITokenReader tokens;
    ASTNode root = new SimpleASTNode(STARTING, "");//文法解析生成的ast树
    Map<String,BigInteger> vars=new HashMap<>(); //用于存储变量
    private String indent=" ";//打印ast树时使用，用于层级的展示
    public SimpleSyntaxParser(){}

    public SimpleSyntaxParser(ITokenReader tokens) {
        this.tokens = tokens;
    }
    public SimpleSyntaxParser(String script) {
        SimpleTokenParser p=new SimpleTokenParser();
        p.setScript(script);
        this.tokens = p.getTokenReader();
    }
    public void setTokens( ITokenReader tokens){
        clean();
        this.tokens=tokens;
    }
    public void setScript(String script){
        clean();
        SimpleTokenParser p=new SimpleTokenParser();
        p.setScript(script);
        this.tokens = p.getTokenReader();
    }
    public void setIndent(String indent){
        this.indent=indent;
    }


    private void clean() {
        tokens=null;
        root=new SimpleASTNode(STARTING, "");
    }

    /**
     * 解析token串，生成ast树
     * statement ::= intDeclarer|assignment|expression
     * 每一个token串都只有上面三种状态中的一个或多个（多个语句时）
     * 依次检测这三种情况即可。
     * @throws Exception
     */
    public void parse() throws Exception {
        while (tokens.peek() != null) {
            Token token = tokens.peek();
            //处理开头是分号，或者连续多个分号的情况
            if (token.getType() == SimpleTokenType.SEMICOLON) {
                tokens.read();
                continue;
            }
            ASTNode node = intDeclare();
            SimpleASTNodeType type=INTDECLARERSTATE;
            if (node == null) {
                node = assignment();
                type=ASSIGNMENTSTATE;
            }
            if (node == null) {
                node = expression();
                type=EXPRESSIONSTATE;
            }
            if (node == null) {
                throw new UnknownOperateException();
            }
            SimpleASTNode result=new SimpleASTNode(type,"");
            result.addChild(node);
            root.addChild(result);
        }


    }
    /**
     * intDeclare::='int' id (';'|=expression)
     * int声明语句只有两种情况
     * eg：int a; int a=1+2;
     * 分别检测即可
     * 第二种情况会检测是否是表达式
     * 第二种是 'int' id = exression
     * 为什么不是 int assignment？因为assignment包含+= -=等类似的操作，这些在初始化的时候不支持
     *
     * 生成的ast树
     *  1. 未初始化，AST(root ::= 'int',index1 ::= AST(root::='=',index1::=id))
     *  2. 完成初始化 AST(root ::= 'int',index1 ::= AST(root::='=',index1::=id,index2::=value))
     *
     * @return
     * @throws Exception
     */
    private ASTNode intDeclare() throws Exception {
        ASTNode root = null; //int
        ASTNode node = null;  //=
        ASTNode child1 = null; //id
        ASTNode child2 = null; //value
        Token token = tokens.peek();
        if (token.getType() == SimpleTokenType.INT) {
            tokens.read();
            Token[] tokenArray = tokens.peek(2);
            if (tokenArray[0].getType() == SimpleTokenType.IDECLARE && tokenArray[1].getType() == SimpleTokenType.SEMICOLON) {
                tokens.read(2);
                root = new SimpleASTNode(INTDECLARER, "int");
                node = new SimpleASTNode(ASSIGNMENT, "=");
                child1 = new SimpleASTNode(ID, tokenArray[0].getText());
                node.addChild(child1);
                root.addChild(node);
            } else if (tokenArray[0].getType() == SimpleTokenType.IDECLARE && tokenArray[1].getType() == SimpleTokenType.ASSIGNMENT) {
                root = new SimpleASTNode(INTDECLARER, "int");
                node = new SimpleASTNode(ASSIGNMENT, "=");
                child1 = new SimpleASTNode(ID, tokenArray[0].getText());
                tokens.read(2);
                child2 = expression();
                node.addChild(child1);
                node.addChild(child2);
                root.addChild(node);
            } else {
                throw new InitDataException();
            }
        }
        return root;
    }

    /**
     * assignment ::= id assign expression
     * 1.AST(root::='=',index1::=id,index2::=value) //a=b
     * 2.AST(root::='=',index1::=id,index2::=AST(root::='+',index1::=id,index2::=value)) //a+=b
     * @return
     * @throws Exception
     */
    private ASTNode assignment() throws Exception {
        ASTNode node = null;
        ASTNode child1 = null;
        ASTNode child2 = null;
        Token[] tokenArr = tokens.peek(2);
        //判断前两个字符是否是 id assign
        //并且排除掉只有单个id的情况
        if (tokenArr.length == 2 && tokenArr[0].getType() == SimpleTokenType.IDECLARE && tokenArr[1].getType() == SimpleTokenType.ASSIGNMENT) {
            tokens.read(2);
            if (tokenArr[1].getText().equals("=")) {//1.AST(root::='=',index1::=id,index2::=value) //a=b
                node = new SimpleASTNode(ASSIGNMENT, "=");
                child1 = new SimpleASTNode(ID, tokenArr[0].getText());
                child2 = expression();
                node.addChild(child1);
                node.addChild(child2);
            } else {////2.AST(root::='=',index1::=id,index2::=AST(root::='+',index1::=id,index2::=value)) //a+=b
                node = new SimpleASTNode(ASSIGNMENT, "=");
                child1 = new SimpleASTNode(ID, tokenArr[0].getText());
                ASTNode child2_1 = new SimpleASTNode(ID, tokenArr[0].getText());
                ASTNode child2_2 = expression();
                SimpleASTNodeType type = getType(tokenArr[1]);
                child2 = new SimpleASTNode(type, type.getText());
                child2.addChild(child2_1);
                child2.addChild(child2_2);
                node.addChild(child1);
                node.addChild(child2);
            }
        }
        return node;
    }

    /**
     * //plus ;
     * 1.AST(root::='+',index1::=id,index2::=value) //可以是'+','-','*','/','%','^','|','&'
     * 2.AST(root::='~',index1::=id) //取反
     * 3.AST(root::='++',index1::=id) //id++
     * 4.AST(root::='=',index1::=id,index2::=AST(root::='+',child1=id,child2=1)) //++id
     * @return
     * @throws Exception
     */
    private ASTNode expression() throws Exception {
        ASTNode node = plus();
        if (tokens.peek()==null||tokens.peek().getType() != SimpleTokenType.SEMICOLON) {
            throw new SemicolonAbsenceException();
        }
        return node;
    }
    /**
     * plus ::= [('+-')*('+')? | ('-+')*('-')?]? andor ([('+-')*('+')? | ('-+')*('-')? ] andor)*
     * 解释下这一坨东西
     * 它的常规形式如下
     * plus ::= andor ('+'|'-' andor)*
     * 为什么变成上面的样子？
     * 1.数字和表达式支持在前面添加任意多个+-号，只要是交替出现即可(连续出现的会被认定为‘++’或者‘--’,这在词法解析阶段已经确定)
     * 2.其实可以完全放到primary()方法中进行处理
     * 3.但是由于primary调用的频率远超过plus的调用频率，为了提高执行效率，所以都放在plus中进行处理
     *
     * 同样说明下FindNextNotPlusMinus类
     * 1.ex属性的说明，当tokens中只剩下‘+’|‘-’的时候，才会设置为true，此时应该触发异常
     * 2.plusMinus属性的说明，连续的多个交替出现的+-，最终的结果是‘+’|‘-’，
     *  当连续出现的个数为4的倍数时，最终结果为 ‘+’
     *  当连续出现的个数除4余1时，最终结果为这一连串符号中最开头的那个
     *  当连续出现的个数除4余2时，最终结果为‘-’
     *  当连续出现的个数除4余3时，最终结果为与开头符号相反的符号
     * 3.当+-出现在plus开头，并且后面是数字或者id的时候，直接将值设置为数字或者id的负值，
     *   AST(root::=(-id|-value))
     *   否则简化为单个+|-运算符
     * @return
     * @throws Exception
     */
    private ASTNode plus() throws Exception {
        boolean state = true;
        Token token = tokens.peek();
        FindNextNotPlusMinus f = null;
        if (token.getType() == SimpleTokenType.PLUS_MINUS) {
            f = new FindNextNotPlusMinus();
            if (f.ex) {
                throw new MeaninglessPlusOrMinusException();
            }
            if (f.plusMinus.equals("-")) {
                state = false;
            }
        }
        ASTNode child1 =null;
        if(state){
            child1=andor();
        }else{
            if(tokens.peek().getType()==SimpleTokenType.NUMBER){
                child1=new SimpleASTNode(VALUE,"-"+tokens.read().getText());
            }else {
                child1 = new SimpleASTNode(MINUS, "-");
                child1.addChild(null);
                child1.addChild(andor());
            }
        }
        ASTNode node = child1;
        while (tokens.peek()!=null&&tokens.peek().getType() == SimpleTokenType.PLUS_MINUS) {
            f = new FindNextNotPlusMinus();
            if (f.ex) {
                throw new MeaninglessPlusOrMinusException();
            }

            ASTNode child2 =andor();
            SimpleASTNodeType type = f.plusMinus.equals("+")?PLUS:MINUS;
            node = new SimpleASTNode(type, type.getText());
            node.addChild(child1);
            node.addChild(child2);
            child1 = node;
        }
        return node;
    }

    //andor ::= mul (and mul)*
    private ASTNode andor() throws Exception {
        ASTNode child1 = mul();
        ASTNode node = child1;
        Token token = null;
        while (tokens.peek()!=null&&(tokens.peek().getType() == SimpleTokenType.AND_OR_XOR )) {
            token = tokens.read();
            SimpleASTNodeType type = getType(token);
            node = new SimpleASTNode(type, type.getText());
            ASTNode child2 = mul();
            node.addChild(child1);
            node.addChild(child2);
            child1 = node;
        }
        return node;
    }
    //mul ::= not(star not)*
    private ASTNode mul() throws Exception {
        ASTNode child1 = not();
        ASTNode node = child1;
        Token token = null;
        while (tokens.peek()!=null&& tokens.peek().getType() == SimpleTokenType.STAR_SLASH_MOD) {
            token = tokens.read();
            SimpleASTNodeType type = getType(token);
            node = new SimpleASTNode(type, type.getText());
            ASTNode child2 = not();
            node.addChild(child1);
            node.addChild(child2);
            child1 = node;
        }
        return node;
    }

    /**
     *  not ::= (~)* primary
     *  可以将多个~简化为1个，但是没进行处理
     *  AST(root::='~',index1::=id)
     */
    private ASTNode not() throws Exception {
        Token token = tokens.peek();
        ASTNode node = null;
        if (token.getType() == SimpleTokenType.NOT) {
            node = new SimpleASTNode(NOT, "~");
            tokens.read();
            node.addChild(not());
        } else {
            node = primary();
        }
        return node;
    }

    /**
     * 说明：
     * 1.数字直接返回相应的astnode即可。
     * 2.(plus)括号必须成对出现
     * 3.id要坚持后面的符号是否是‘++’|‘--’,是的话则按照 id++|id--的规则生成astnode，否则直接生成astnode即可
     * 4.‘++’|‘--’的情况，如果后面是非id的话，报错；是id的话，按照 id =id + 1 生成astnode
     * @return
     * @throws Exception
     */
    private ASTNode primary() throws Exception {
        Token token = tokens.peek();
        if(token==null){
            throw new UnknownOperateException();
        }
        ASTNode node = null;
        ASTNode child1 = null;
        ASTNode child2 = null;
        if (token.getType() == SimpleTokenType.IDECLARE) {
            Token t = tokens.read();
            token = tokens.peek();
            if (token!=null&&(token.getType() == SimpleTokenType.PLUS2 || token.getType() == SimpleTokenType.MINUS2)) {
                tokens.read();
                node = new SimpleASTNode(getType(token), token.getText());
                child1 = new SimpleASTNode(ID, t.getText());
                node.addChild(child1);
            } else {
                node = new SimpleASTNode(ID, t.getText());
            }
        } else if (token.getType() == SimpleTokenType.NUMBER) {
            tokens.read();
            node = new SimpleASTNode(VALUE, token.getText());
        } else if (token.getType() == SimpleTokenType.PARENTHESES && token.getText().equals("(")) {
            tokens.read();
            node = plus();
            token = tokens.peek();
            if (token!=null&&token.getType() != SimpleTokenType.PARENTHESES && !token.getText().equals(")")) {
                throw new RightParentheseLostException();
            }
            tokens.read();
        } else if (token.getType() == SimpleTokenType.PLUS2 || token.getType() == SimpleTokenType.MINUS2) {
            Token t = tokens.read();
            token = tokens.peek();
            if (token.getType() != SimpleTokenType.IDECLARE) {
                throw new NeedIdButGetOtherException();
            }
            tokens.read();
            node=new SimpleASTNode(ASSIGNMENT,"=");
            child1 = new SimpleASTNode(ID, token.getText());
            SimpleASTNodeType type=t.getType()==SimpleTokenType.PLUS2?PLUS:MINUS;
            child2 = new SimpleASTNode(type,type.getText());
            child2.addChild(new SimpleASTNode(ID,token.getText()));
            child2.addChild(new SimpleASTNode(VALUE,"1"));
            node.addChild(child1);
            node.addChild(child2);
        } else {
            throw new UnknownOperateException();
        }

        return node;
    }

    private SimpleASTNodeType getType(Token token) {
        if (token.getType() == SimpleTokenType.ASSIGNMENT) {
            return mapping().get(token.getText());
        }
        if (token.getType() == SimpleTokenType.PLUS_MINUS) {
            return token.getText().equals("+") ? PLUS : MINUS;
        }
        if (token.getType() == SimpleTokenType.AND_OR_XOR) {
            return token.getText().equals("&") ? AND : (token.getText().equals("|") ? OR : XOR);
        }
        if (token.getType() == SimpleTokenType.STAR_SLASH_MOD) {
            return token.getText().equals("*") ? STAR : (token.getText().equals("/") ? SLASH : MOD);
        }
        if (token.getType() == SimpleTokenType.PLUS2) {
            return PLUS2;
        }
        if (token.getType() == SimpleTokenType.MINUS2) {
            return MINUS2;
        }

        return null;
    }

    private Map<String, SimpleASTNodeType> mapping() {
        Map<String, SimpleASTNodeType> map = new HashMap();
        map.put("+=", PLUS);
        map.put("-=", MINUS);
        map.put("=", ASSIGNMENT);
        map.put("*=", STAR);
        map.put("/=", SLASH);
        map.put("%=", MOD);
        map.put("&=", AND);
        map.put("|=", OR);
        map.put("^=", XOR);
        return map;
    }
    public void tree(){
        System.out.println(tree((SimpleASTNode) root,indent,0));
    }
    private String tree(SimpleASTNode node,String indent,int i){
        StringBuilder result=new StringBuilder();
        result.append(getIndent(indent,i)).append(node.type.toString()+":"+node.value).append(System.lineSeparator());
        for(ASTNode n:node.getChildren()){
            if(n==null){
                result.append(getIndent(indent,i+1)).append("null").append(System.lineSeparator());
                continue;
            }
            SimpleASTNode sn=(SimpleASTNode) n;
            result.append(tree(sn,indent,i+1));
        }
        return result.toString();
    }
    private String getIndent(String indent,int n){
        StringBuilder result=new StringBuilder();
        for(int i=0;i<n;i++){
            result.append(indent);
        }
        return result.toString();
    }

    class FindNextNotPlusMinus {
        Integer startPos = 0;
        Integer endPos = 0;
        boolean ex;
        String plusMinus;
        String first;

        FindNextNotPlusMinus() {
            startPos = tokens.getPos();
            Token t = tokens.peek();
            first = t.getText();
            while (t != null && t.getType() == SimpleTokenType.PLUS_MINUS) {
                tokens.read();
                t = tokens.peek();
                if (t == null) {
                    ex = true;
                    break;
                }
            }
            endPos = tokens.getPos();
            int num = endPos - startPos;
            if (num % 4 == 0) {
                plusMinus = "+";
            } else if (num % 4 == 1) {
                plusMinus = first;
            } else if (num % 4 == 2) {
                plusMinus = "-";
            } else {
                plusMinus = first.equals("+") ? "-" : "+";
            }
        }
    }
    public void calculate(){
        if(root==null||root.getChildren().size()==0){
            System.out.println();
            return;
        }
        for(int i=0;i<root.getChildren().size();i++){
            SimpleASTNode node=(SimpleASTNode)root.getChild(i);
            SimpleASTNodeType type=node.getType();
            try {
                if (type == EXPRESSIONSTATE) {
                    System.out.println(calculate(node.getChild(0)).toString());
                }
                if (type == ASSIGNMENTSTATE) {
                    String id = node.getChild(0).getChild(0).getText();
                    if (!vars.keySet().contains(id)) {
                        throw new IdNotDeclaredException(id);
                    } else {
                        BigInteger value = calculate(node.getChild(0).getChild(1));
                        System.out.println(id + " = " + value);
                    }
                }
                if (type == INTDECLARERSTATE) {
                    ASTNode intNode = node.getChild(0).getChild(0);
                    String id = intNode.getChild(0).getText();
                    if (vars.containsKey(id)) {
                        throw new IdHasDeclaredException(id);
                    }
                    if (intNode.getChildren().size() == 1) {
                        vars.put(id, null);
                        System.out.println();
                    } else {
                        BigInteger value = calculate(intNode.getChild(1));
                        vars.put(id, value);
                        System.out.println("int " + id + " = " + value);
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    }
    private BigInteger calculate(ASTNode node) throws Exception{
        SimpleASTNodeType type= (SimpleASTNodeType) node.getType();
        String value=node.getText();
        if(type==VALUE){
            return new BigInteger(value);
        }
        if(type==ID){
            if(vars.containsKey(value)){
                if(vars.get(value)==null){
                    throw new IdNotInitedException(value);
                }
                return vars.get(value);
            }else{
                throw new IdNotDeclaredException(value);
            }
        }
        if(type==PLUS){
            return calculate(node.getChild(0)).add(calculate(node.getChild(1)));
        }
        if(type==MINUS){
            return calculate(node.getChild(0)).subtract(calculate(node.getChild(1)));
        }
        if(type==STAR){
            return calculate(node.getChild(0)).multiply(calculate(node.getChild(1)));
        }if(type==SLASH){
            return calculate(node.getChild(0)).divide(calculate(node.getChild(1)));
        }
        if(type==MOD){
            return calculate(node.getChild(0)).mod(calculate(node.getChild(1)));
        }
        if(type==AND){
            return calculate(node.getChild(0)).and(calculate(node.getChild(1)));
        }
        if(type==OR){
            return calculate(node.getChild(0)).or(calculate(node.getChild(1)));
        }if(type==XOR){
            return calculate(node.getChild(0)).xor(calculate(node.getChild(1)));
        }
        if(type==NOT){
            return calculate(node.getChild(0)).not();
        }
        if(type==PLUS2||type==MINUS2){
            value=node.getChild(0).getText();
            if(vars.containsKey(value)){
                if(vars.get(value)==null){
                    throw new IdNotInitedException(value);
                }
                BigInteger result= vars.get(value);
                BigInteger one=new BigInteger("1");
                vars.put(value,type==PLUS2?result.add(one):result.subtract(one));
                return result;
            }else{
                throw new IdNotDeclaredException(value);
            }
        }
        throw new UnknownOperateException();
    }

    public static void main(String[] args) throws Exception {
        SimpleTokenParser parse = new SimpleTokenParser();
        parse.setScript("a+b");
        SimpleSyntaxParser syntax = new SimpleSyntaxParser(parse.getTokenReader());
        syntax.parse();
        String s=syntax.tree((SimpleASTNode) syntax.root,"   ",0);
        System.out.println(s);
        /*syntax.calculate();*/
    }
}
