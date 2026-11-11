package bank.exception;

 public class MinimumDepositException extends BankingException {
    private static final long serialVersionUID = 1l;
    public MinimumDepositException(String message) {
    super(message);
    }
}
