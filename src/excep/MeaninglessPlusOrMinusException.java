package excep;

public class MeaninglessPlusOrMinusException extends Exception {
    public MeaninglessPlusOrMinusException(){
        super("There exists meaningless plus or minus");
    }
}
