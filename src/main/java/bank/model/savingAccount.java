package bank.model;
import bank.exception.InvalidAmountException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Savings accounts earn 0.05% interest and don't implement withdrawal interface */


public class savingAccount extends Account implements InterestBearing {

    public static final BigDecimal INTEREST_PER_ANNUM = new BigDecimal("0.0005"); 
    private YearMonth lastInterestCalculation;

    public savingAccount(String accountNumber, String branchName,BigDecimal openingDeposit) throws InvalidAmountException {
        super(accountNumber, branchName, openingDeposit);
        
    }

    public savingAccount(String accountNumber, String branchName, LocalDate dateOpened, BigDecimal balance, List<Transaction> storedTransactions, YearMonth lastInterestCalculation) {
        super(accountNumber, branchName, dateOpened, balance, storedTransactions);
        this.lastInterestCalculation= lastInterestCalculation;

}

@Override
public String getAccountType(){
    return "Savings Account";

}

@Override
public BigDecimal computeInterestperMonth() {
    return interestAt(INTEREST_PER_ANNUM);
}
@Override
public void InterestPayment() {
    InterestPayment(YearMonth.now());
}
 @Override
public boolean InterestPayment(YearMonth currentMonth){
    if (currentMonth.equals(lastInterestCalculation)){
        return false;
    }
    creditInterest(computeInterestperMonth());
    lastInterestCalculation= currentMonth;
    return true;
}  
 
 @Override
    public YearMonth getLastInterestCalculation() {
        return lastInterestCalculation;
    }
}