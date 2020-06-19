package excep;

public class IdHasDeclaredException extends Exception{
    public IdHasDeclaredException(String id){
        super(id + " has already been  declared");
    }
}
