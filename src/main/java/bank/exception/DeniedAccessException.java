package bank.exception;

/** when a signed in user isn't allowed to do what they requested this is thrown, or when the user isn't signed in */

public class DeniedAccessException extends BankingException {
    private static final long serialVersionUID = 1l;

    public DeniedAccessException(String message) {
        super(message);
    }
}