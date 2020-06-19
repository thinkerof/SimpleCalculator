package excep;

public class RightParentheseLostException extends Exception {
    public RightParentheseLostException() {
        super("The ')' missed");
    }
}
