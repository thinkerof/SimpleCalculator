package compiler_simpleCalculator;

import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        boolean printTree=false;
        String indent=" ";
        String inputPrefix="In:";
        String outputPrefix="Out:";
        System.out.print(inputPrefix);
        Scanner scanner=new Scanner(System.in);
        String s;
        SimpleSyntaxParser parser=new SimpleSyntaxParser();
        parser.setIndent(indent);
        while((s=scanner.nextLine())!=null&&!s.equals("exit()")){
            if("".equals(s)){
                System.out.print(inputPrefix);
                continue;
            }
            if(s.equals("openTree()")){
                printTree=true;
                System.out.print(inputPrefix);
                continue;
            }
            if(s.equals("closeTree()")){
                printTree=false;
                System.out.print(inputPrefix);
                continue;
            }
            if(parser.vars.containsKey(s)){
                System.out.println(outputPrefix+parser.vars.get(s));
                System.out.print(inputPrefix);
                continue;
            }
            parser.setScript(s);
            try {
                parser.parse();
            }catch (Exception e){
                if(e.getMessage()!=null){
                    System.out.println(outputPrefix+e.getMessage());
                }else{
                    System.out.println(outputPrefix+e.getClass().getName());
                }
                System.out.print(inputPrefix);
                continue;
            }
            if(printTree){
                parser.tree();
            }
            System.out.print(outputPrefix);
            parser.calculate();
            System.out.print(inputPrefix);

        }







    }
}
