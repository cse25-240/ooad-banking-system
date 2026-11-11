package bank.model;

import bank.exception.BankingException;
import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import bank.exception.MinimumDepositException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;


/**this account needs an opening deposit of P500, one can deposit and withdraw.
 * earns interest of 5% per annum, interest is calculated monthly 
 */

public class investmentAccount extends Account implements InterestBearing, Withdrawable {
  public static final BigDecimal MINIMUM_OPENING_DEPOSIT = new BigDecimal("500.00"); 
    public static final BigDecimal INTEREST_PER_ANNUM = new BigDecimal("0.05");   

  private YearMonth lastInterestCalculation;

    public investmentAccount(String accountNumber, String branchName, BigDecimal openingDeposit) throws BankingException {
        super(accountNumber, branchName, requireMinimum(openingDeposit));
    } 
    
    public investmentAccount(String accountNumber, String branchName, LocalDate dateOpened, BigDecimal balance,
    List<Transaction>storedTransactions, YearMonth lastInterestCalculation) {
        super(accountNumber, branchName,dateOpened, balance, storedTransactions);
        this.lastInterestCalculation = lastInterestCalculation;
    }



    private static BigDecimal requireMinimum(BigDecimal openingDeposit) throws BankingException {
        BigDecimal money = Account.toMoney(openingDeposit);
        if (money.compareTo(MINIMUM_OPENING_DEPOSIT) < 0) {
            throw new MinimumDepositException("Opening deposit must be at least " + MINIMUM_OPENING_DEPOSIT);
        }
        return money;
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }

    @Override
    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        debit(amount);
    }

    @Override
    public BigDecimal computeInterestperMonth() {
        return interestAt(INTEREST_PER_ANNUM);
    }

    @Override
    public void InterestPayment() {
        InterestPayment(YearMonth.now());
    } 

    /** Pays the current month's interest(returns true if paid) but does nothing if it was already paid(returns false). */
    @Override
    public boolean InterestPayment(YearMonth currentMonth) {
        if (currentMonth.equals(lastInterestCalculation)) {
            return false; // Interest already paid for this month
        }
        creditInterest(computeInterestperMonth());
        lastInterestCalculation = currentMonth;
        return true;
    }
     @Override
     public YearMonth getLastInterestCalculation() {
        return lastInterestCalculation;
} 
} 