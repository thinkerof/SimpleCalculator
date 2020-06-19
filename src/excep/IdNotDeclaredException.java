package excep;

public class IdNotDeclaredException extends Exception {
    public IdNotDeclaredException(String id){
        super("ID has not been declared,ID='"+id+"';");
    }
    public IdNotDeclaredException(){
        super("ID has not been declared;");
    }
}
