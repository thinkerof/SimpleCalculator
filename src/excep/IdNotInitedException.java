package excep;

public class IdNotInitedException extends Exception {
    public IdNotInitedException(String id){
        super("ID has not been declared,ID='"+id+"';");
    }

}
