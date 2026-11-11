package bank.exception;

public class InsufficientFundsException extends BankingException {
    private static final long serialVersionUID = 1l;
    public InsufficientFundsException(String message) {
        super(message);
    }

    }

