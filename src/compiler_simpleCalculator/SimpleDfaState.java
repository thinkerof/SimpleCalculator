package compiler_simpleCalculator;

/**
 * 用于记录Token解析时的中间状态
 */
public enum  SimpleDfaState {
    ID,
    ID_INT1, //i
    ID_INT2, //in
    ID_INT3, //int
    NUMBER,
    ASSIGNMENT,//=
    PLUS_MINUS,//+ -
    STAR_SLASH_MOD,//* / %
    NOT, // ~
    AND_OR_XOR, // & | ^
    LEFT_PARENTHESIS,// (
    RIGHT_PARENTHESIS,// )
    SEMICOLON, // ;
    PLUS2,//++
    MINUS2//--
}
