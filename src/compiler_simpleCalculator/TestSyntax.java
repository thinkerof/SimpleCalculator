package compiler_simpleCalculator;

import compiler.ASTNode;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static compiler_simpleCalculator.SimpleASTNodeType.*;

public class TestSyntax {

    public static void main(String[] args) throws Exception{
        SimpleSyntaxParser syntaxParser=new SimpleSyntaxParser();
        for(String s:syntax.keySet()){
            try {
                Predicate<ASTNode> p = syntax.get(s);
                if (p != null) {
                    syntaxParser.setScript(s);
                    syntaxParser.parse();
                    if (!p.test(syntaxParser.root)) {
                        System.out.println(s+"========");
                    }
                } else {
                    syntaxParser.setScript(s);
                    syntaxParser.parse();
                }
            }catch (Exception e){
                if(errors.contains(s)){
                    errors.remove(s);
                }else {
                    System.out.println(s);
                }
            }
        }
        System.out.println(errors);
    }

    private static String test_expression_right_1="a;";
    private static Predicate<ASTNode> test_expression_right_1_p=
            parse(Arrays.asList(ID.toString()+":a;"));
    private static String test_expression_right_2="1;";
    private static Predicate<ASTNode> test_expression_right_2_p=
            parse(Arrays.asList(VALUE.toString()+":1;"));
    private static String test_expression_right_3="~1;";
    private static Predicate<ASTNode> test_expression_right_3_p=
            parse(Arrays.asList(NOT.toString()+":;"+VALUE.toString()+":1;"));
    private static String test_expression_right_4="1*2;";
    private static Predicate<ASTNode> test_expression_right_4_p=
            parse(Arrays.asList(STAR.toString()+":;"
                    +VALUE.toString()+":1;"+VALUE.toString()+":2;"));
    private static String test_expression_right_5="1+2;";
    private static Predicate<ASTNode> test_expression_right_5_p=
            parse(Arrays.asList(PLUS.toString()+":;"
                    +VALUE.toString()+":1;"+VALUE.toString()+":2;"));
    private static String test_expression_right_6="1+2+3;";
    private static Predicate<ASTNode> test_expression_right_6_p=
            parse(Arrays.asList(PLUS.toString()+":;"+PLUS.toString()+":;"
                    +VALUE.toString()+":1;"+VALUE.toString()+":2;"+VALUE.toString()+":3;"));
    private static String test_expression_right_7="2*3+1;";
    private static Predicate<ASTNode> test_expression_right_7_p=
            parse(Arrays.asList(PLUS.toString()+":;"+STAR.toString()+":;"
                    +VALUE.toString()+":2;"+VALUE.toString()+":3;"+VALUE.toString()+":1;"));
    private static String test_expression_right_8="2*3+~1;";
    private static Predicate<ASTNode> test_expression_right_8_p=
            parse(Arrays.asList(PLUS.toString()+":;"+STAR.toString()+":;"+VALUE.toString()+":2;"
                    +VALUE.toString()+":3;"+NOT.toString()+":;"+VALUE.toString()+":1;"));
    private static String test_expression_right_9="~2*3+~1;";
    private static Predicate<ASTNode> test_expression_right_9_p=
            parse(Arrays.asList(PLUS.toString()+":;"+STAR.toString()+":;"+NOT.toString()+":;"
                    +VALUE.toString()+":2;"+VALUE.toString()+":3;"+NOT.toString()+":;"+VALUE.toString()+":1;"));
    private static String test_expression_right_10="a*b+c;";
    private static Predicate<ASTNode> test_expression_right_10_p=
            parse(Arrays.asList(PLUS.toString()+":;"+STAR.toString()+":;"
                    +ID.toString()+":a;"+ID.toString()+":b;"+ID.toString()+":c;"));
    private static String test_expression_right_11="2+(3-4);";
    private static Predicate<ASTNode> test_expression_right_11_p=
            parse(Arrays.asList(PLUS.toString()+":;"+VALUE.toString()+":2;"
                    +MINUS.toString()+":;"+VALUE.toString()+":3;"+VALUE.toString()+":4;"));
    private static String test_expression_right_12="a++;";
    private static Predicate<ASTNode> test_expression_right_12_p=
            parse(Arrays.asList(PLUS2.toString()+":;"+ID.toString()+":a;"));
    private static String test_expression_right_13="a--;";
    private static Predicate<ASTNode> test_expression_right_13_p=
            parse(Arrays.asList(MINUS2.toString()+":;"+ID.toString()+":a;"));
    private static String test_expression_right_14="2+-+-+1;";
    private static Predicate<ASTNode> test_expression_right_14_p=
            parse(Arrays.asList(PLUS.toString()+":;"
                    +VALUE.toString()+":2;"+VALUE.toString()+":1;"));
    private static String test_expression_right_15="2+-+-+-1;";
    private static Predicate<ASTNode> test_expression_right_15_p=
            parse(Arrays.asList(MINUS.toString()+":;"
                    +VALUE.toString()+":2;"+VALUE.toString()+":1;"));
    private static String test_expression_right_16="a+++-+-1;";
    private static Predicate<ASTNode> test_expression_right_16_p=
            parse(Arrays.asList(PLUS.toString()+":;"+PLUS2+":;"+ID.toString()+":a;"
                    +VALUE.toString()+":1;"));
    private static String test_expression_right_17="++a+-+-b--;";
    private static Predicate<ASTNode> test_expression_right_17_p=
            parse(Arrays.asList(PLUS.toString()+":;"+ASSIGNMENT.toString()+":;"+ID.toString()+":a;"
                    +PLUS.toString()+":;"+ID.toString()+":a;"+VALUE.toString()+":1;"+MINUS2+":;"+ID.toString()+":b;"));
    private static String test_expression_right_18="a+++(b++*3+-+-+-~c--&d);";
    private static Predicate<ASTNode> test_expression_right_18_p=
            parse(Arrays.asList(PLUS.toString()+":;"+PLUS2.toString()+":;"+ID.toString()+":a;"+
                    MINUS.toString()+":;"+STAR.toString()+":;"+PLUS2.toString()+":;"+ID.toString()+":b;"+VALUE.toString()+":3;" +
                    AND.toString()+":;"+NOT.toString()+":;"+MINUS2.toString()+":;"+ID.toString()+":c;"+ID.toString()+":d;"));
    private static String test_expression_right_19="+-1+-+-+-(-+1+-+-+-10);";
    private static Predicate<ASTNode> test_expression_right_19_p=
            parse(Arrays.asList(MINUS.toString()+":;"+VALUE.toString()+":-1;"
                    +MINUS.toString()+":;"+VALUE.toString()+":-1;"+VALUE.toString()+":10;"));
    private static String test_expression_error_1="a";
    private static String test_expression_error_2="1";
    private static String test_expression_error_3="~1";
    private static String test_expression_error_4="1*2";
    private static String test_expression_error_5="1++;";
    private static String test_expression_error_6="++1;";
    private static String test_expression_error_7="2+++-+1;";
    private static String test_expression_error_8="2+++-+1;";
    private static String test_expression_error_9="2+-++-+1;";
    private static String test_expression_error_10="2+-++-+1;";

    private static String test_assignment_right_1="a=1;";
    private static Predicate<ASTNode> test_assignment_right_1_p=
            parse(Arrays.asList(ASSIGNMENT.toString()+":;"+ID.toString()+":a;"+VALUE.toString()+":1;"));
    private static String test_assignment_right_2="a+=1;";
    private static Predicate<ASTNode> test_assignment_right_2_p=
            parse(Arrays.asList(ASSIGNMENT.toString()+":;"+ID.toString()
                    +":a;"+PLUS+":;"+ID.toString()+":a;"+VALUE+":1;"));
    private static String test_assignment_right_3="a+=b;";
    private static Predicate<ASTNode> test_assignment_right_3_p=
            parse(Arrays.asList(ASSIGNMENT.toString()+":;"+ID.toString()
                    +":a;"+PLUS+":;"+ID.toString()+":a;"+ID.toString()+":b;"));

    private static String test_assignment_error_1="a=1";

    private static String test_intDeclarer_right_1="int a;";
    private static Predicate<ASTNode> test_intDeclarer_right_1_p=
            parse(Arrays.asList(INTDECLARER.toString()+":;"+ASSIGNMENT.toString()+":;"+ID.toString()+":a;"));
    private static String test_intDeclarer_right_2="int a=1;";
    private static Predicate<ASTNode> test_intDeclarer_right_2_p=
            parse(Arrays.asList(INTDECLARER.toString()+":;"+ASSIGNMENT.toString()+":;"+ID.toString()+":a;"+VALUE.toString()+":1;"));
    private static String test_intDeclarer_error_1="int a";
    private static String test_intDeclarer_error_2="int a=1";
    private static Map<String, Predicate<ASTNode>> syntax=new HashMap<>();
    private static Set<String> errors=new HashSet<>();
    static {
        syntax.put(test_expression_right_1,test_expression_right_1_p);
        syntax.put(test_expression_right_2,test_expression_right_2_p);
        syntax.put(test_expression_right_3,test_expression_right_3_p);
        syntax.put(test_expression_right_4,test_expression_right_4_p);
        syntax.put(test_expression_right_5,test_expression_right_5_p);
        syntax.put(test_expression_right_6,test_expression_right_6_p);
        syntax.put(test_expression_right_7,test_expression_right_7_p);
        syntax.put(test_expression_right_8,test_expression_right_8_p);
        syntax.put(test_expression_right_9,test_expression_right_9_p);
        syntax.put(test_expression_right_10,test_expression_right_10_p);
        syntax.put(test_expression_right_11,test_expression_right_11_p);
        syntax.put(test_expression_right_12,test_expression_right_12_p);
        syntax.put(test_expression_right_13,test_expression_right_13_p);
        syntax.put(test_expression_right_14,test_expression_right_14_p);
        syntax.put(test_expression_right_15,test_expression_right_15_p);
        syntax.put(test_expression_right_16,test_expression_right_16_p);
        syntax.put(test_expression_right_17,test_expression_right_17_p);
        syntax.put(test_expression_right_18,test_expression_right_18_p);
        syntax.put(test_expression_right_19,test_expression_right_19_p);
        syntax.put(test_assignment_right_1,test_assignment_right_1_p);
        syntax.put(test_assignment_right_2,test_assignment_right_2_p);
        syntax.put(test_assignment_right_3,test_assignment_right_3_p);
        syntax.put(test_intDeclarer_right_1,test_intDeclarer_right_1_p);
        syntax.put(test_intDeclarer_right_2,test_intDeclarer_right_2_p);

        syntax.put(test_expression_error_1,null);
        syntax.put(test_expression_error_2,null);
        syntax.put(test_expression_error_3,null);
        syntax.put(test_expression_error_4,null);
        syntax.put(test_expression_error_5,null);
        syntax.put(test_expression_error_6,null);
        syntax.put(test_expression_error_7,null);
        syntax.put(test_expression_error_8,null);
        syntax.put(test_expression_error_9,null);
        syntax.put(test_expression_error_10,null);
        syntax.put(test_assignment_error_1,null);
        syntax.put(test_intDeclarer_error_1,null);
        syntax.put(test_intDeclarer_error_2,null);
        errors.addAll(syntax.keySet().stream().filter(s->syntax.get(s)==null).collect(Collectors.toSet()));
    }

    /**
     * []
     * type:value;children
     * 深度优先
     * @return
     */
    private static Predicate<ASTNode> parse(List<String> list){
        Predicate<ASTNode> result=s->{
            List<ASTNode> node=s.getChildren();
            if(node.size()!=list.size()){
                return false;
            }
            for(int i=0;i<node.size();i++){
                if(!get(node.get(i)).equals(list.get(i))){
                    System.out.println(get(node.get(i)));
                    System.out.println(list.get(i));
                    return false;
                }
            }
            return  true;
        };
        return result;
    }
    private static String get(ASTNode node){
        StringBuilder result=new StringBuilder();
        if(node==null){
            return "NULL;";
        }
        if(node.getType()==INTDECLARERSTATE||node.getType()==ASSIGNMENTSTATE||node.getType()==EXPRESSIONSTATE){
             return get(node.getChild(0));
        }
        result.append(node.getType().toString());
        result.append(":");
        if(node.getType()==VALUE){
            result.append(node.getText());
        }
        if(node.getType()==ID){
            result.append(node.getText());
        }
        result.append(";");
        List<ASTNode> children=node.getChildren();
        if(children!=null&&children.size()!=0){
            for(int i=0;i<children.size();i++){
                result.append(get(children.get(i)));
            }
        }
        return result.toString();
    }


}
