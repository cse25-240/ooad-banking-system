package bank.exception;

public class InvalidAmountException extends BankingException {
    private static final long serialVersionUID = 1l;
    public InvalidAmountException(String message) {
       
    super(message);
    }
}
