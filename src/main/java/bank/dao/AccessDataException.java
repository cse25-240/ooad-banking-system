package bank.dao;
 

public class AccessDataException extends RuntimeException {
    private static final long serialVersionUID = 1l;

    public AccessDataException(String message){
        super(message);
    }
    public AccessDataException(String message, Throwable cause){
        super(message, cause);
    }
} 