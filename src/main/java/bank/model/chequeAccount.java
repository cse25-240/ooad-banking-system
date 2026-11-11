package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**cheque accounts can only be held by salaried individuals and earns no interest
 * Employer details are reuired whin creating a cheque account(in constructor) or it can't be made
 */
public class chequeAccount extends Account implements Withdrawable {
   
   private final Employer employer;

    public chequeAccount(String accountNumber, String branchName, BigDecimal openingDeposit, Employer employer) 
    throws InvalidAmountException{
        super(accountNumber, branchName, openingDeposit);
        if (employer == null) {
            throw new IllegalArgumentException("Employer details are required for a cheque account");
        }
        this.employer = employer;
    }

public chequeAccount(String accountNumber, String branchName, LocalDate openedOn, Employer employer,BigDecimal balance, List<Transaction> storedTransactions) {
    super(accountNumber, branchName, openedOn, balance, storedTransactions);
    this.employer = employer;
}   

    @Override
    public String getAccountType() {
        return "Cheque Account";
    }

    @Override
    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        debit(amount);
    }

    public Employer getEmployer() {
        return employer;
    }
    
}