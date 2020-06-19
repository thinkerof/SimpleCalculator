package compiler_simpleCalculator;

import compiler.Token;
import compiler.TokenType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static compiler_simpleCalculator.SimpleTokenType.*;

public class TestToken {

    public static void main(String[] args) {

        for (String key : scripts.keySet()) {
            stp.setScript(key);
            if (!scripts.get(key).test(stp.list())) {
                System.out.println(key);
                System.out.println(stp.list());
            }
        }
    }


    private static SimpleTokenParser stp = new SimpleTokenParser();
    private static String test_computer_symbol = "+-*/%^|&!";
    private static String test_assignment = "= += -= /= *= |= ^= &= %=";
    private static String test_number_right = "123;123,456;123_456_7_8_9;";
    private static String test_number_wrong_1 = "1_2,3";
    private static String test_number_wrong_2 = "123_";
    private static String test_number_wrong_3 = "123,,3";
    private static String test_id_1 = "abc a_bc _abc 99abc";
    private static String test_id_2 = ",abc";
    private static String test_key_words = "int a=1";
    private static String test_plus2 = "++ --";
    private static String test_other = "();";
    private static String test_all = "int inta=1;inta++;inta+=3*(4-5);";
    private static Map<String, Predicate<List<Token>>> scripts = new HashMap<>();

    static {
        scripts.put(test_computer_symbol,
                s -> s.size() == 9 && isRightToken(s, Arrays.asList(PLUS_MINUS, PLUS_MINUS, STAR_SLASH_MOD, STAR_SLASH_MOD, STAR_SLASH_MOD, AND_OR_XOR, AND_OR_XOR, AND_OR_XOR, NOT)));
        scripts.put(test_assignment,
                s -> s.size() == 9 && s.stream().allMatch(k -> k.getType() == ASSIGNMENT));
        scripts.put(test_number_right,
                s -> s.size() == 6
                        && Arrays.asList(s.get(0), s.get(2), s.get(4)).stream().allMatch(k -> k.getType() == NUMBER)
                        && s.get(2).getText().equals("123456")
                        && s.get(4).getText().equals("123456789"));
        scripts.put(test_number_wrong_1, s -> s.size() == 0);
        scripts.put(test_number_wrong_2, s -> s.size() == 0);
        scripts.put(test_number_wrong_3, s -> s.size() == 0);
        scripts.put(test_id_1, s -> s.size() == 4 && s.stream().allMatch(k -> k.getType() == IDECLARE));
        scripts.put(test_id_2, s -> s.size() == 1 && s.get(0).getText().equals("abc"));
        scripts.put(test_key_words, s -> s.get(0).getType() == INT);
        scripts.put(test_plus2, s -> s.size() == 2 && s.get(0).getType() == PLUS2 && s.get(1).getType() == MINUS2);
        scripts.put(test_other, s -> s.size() == 3 && isRightToken(s, Arrays.asList(PARENTHESES, PARENTHESES, SEMICOLON)));
        scripts.put(test_all, s -> s.size() == 18 && isRightToken(s, Arrays.asList(
                INT, IDECLARE, ASSIGNMENT, NUMBER, SEMICOLON,
                IDECLARE, PLUS2, SEMICOLON,
                IDECLARE, ASSIGNMENT, NUMBER, STAR_SLASH_MOD, PARENTHESES, NUMBER, PLUS_MINUS, NUMBER, PARENTHESES, SEMICOLON
        )));
    }

    ;

    private static boolean isRightToken(List<Token> origin, List<TokenType> target) {
        if (origin == null || target == null) {
            return false;//不会处理到两种都是null的这种情况
        }
        if (origin.size() != target.size()) {
            return false;
        }
        for (int i = 0; i < origin.size(); i++) {
            if (origin.get(i).getType() != target.get(i)) {
                return false;
            }
        }
        return true;
    }

}
