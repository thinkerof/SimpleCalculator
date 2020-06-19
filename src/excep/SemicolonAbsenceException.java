package excep;

public class SemicolonAbsenceException extends Exception {
    public SemicolonAbsenceException(){
        super("Semicolon absence at the end of expression");
    }
}
