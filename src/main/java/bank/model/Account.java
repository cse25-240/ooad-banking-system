package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
  private final String accountNumber;
  private BigDecimal balance = BigDecimal.ZERO.setScale(2);
  private final String branchName;
  private final List<Transaction> transactions = new ArrayList<>();
  private final LocalDate dateOpened;

  protected Account(String accountNumber, String branchName, BigDecimal initialDeposit)
      throws InvalidAmountException {
    this.accountNumber = Employer.requireText(accountNumber, "Account number");
    this.branchName = Employer.requireText(branchName, "Branch name");
    this.dateOpened = LocalDate.now();

    BigDecimal first = toMoney(initialDeposit);
    if (first.signum() < 0) {
      throw new InvalidAmountException("Initial deposit must be non-negative");
    }
    if (first.signum() > 0) {
      applyThat(TransactionType.DEPOSIT, first, first, "Initial deposit");
    }
  }


  protected Account(String accountNumber, String branchname, LocalDate dateOpened, BigDecimal balance, List<Transaction> storedTransactions){
    this.accountNumber= accountNumber;
    this.branchName= branchname;
    this.dateOpened= dateOpened;
    this.balance= balance.setScale(2, RoundingMode.HALF_UP);
    this.transactions.addAll(storedTransactions);
  }

  /**Displays the account type name */
  public abstract String getAccountType();

  public void deposit(BigDecimal amount) throws InvalidAmountException {
    deposit(amount, "Deposit");
  }

  public void deposit(BigDecimal amount, String description) throws InvalidAmountException {
    BigDecimal money = toPositiveMoney(amount);
    applyThat(TransactionType.DEPOSIT, money, balance.add(money), description);
  }

  /**Withdraws money, called by classes that implement the withdrawable interface */
  protected final void debit(BigDecimal amount)
      throws InvalidAmountException, InsufficientFundsException {
    BigDecimal money = toPositiveMoney(amount);
    if (balance.compareTo(money) < 0) {
      throw new InsufficientFundsException(
          "Insufficient funds for withdrawal: Balance is " + balance +
              ", attempted withdrawal is BWP" + money);
    }
    applyThat(TransactionType.WITHDRAWAL,money, balance.subtract(money), "Withdrawal");
  }

  /** Adds interest to the balance. */
  protected final void creditInterest(BigDecimal interest) {
    if (interest.signum() <= 0) {
      return;
    }
    applyThat(TransactionType.INTEREST, interest, balance.add(interest), "Interest Per month");
  }

  protected final BigDecimal interestAt(BigDecimal monthlyRate) {
    return balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
  }

  /**accessors */

  public String getAccountNumber() {
    return accountNumber;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public String getBranchName() {
    return branchName;
  }

  public LocalDate getDateOpened() {
    return dateOpened;
  }

/** classes that call this can't add or edit anything */
  public List<Transaction> getTransactions() {
    return Collections.unmodifiableList(transactions);
  }


  private void applyThat(TransactionType type, BigDecimal amount, BigDecimal newBalance, String description){
    balance = newBalance;
    transactions.add(new Transaction(accountNumber, amount, description, newBalance, type));

  }

  /** this is to recieve two decimal places and returns the amount at two */
  static BigDecimal toMoney(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount is needed");
    }
     
    if (amount.stripTrailingZeros().scale() > 2) {
      throw new IllegalArgumentException("Amount must have at most two decimal places");
    }
    return amount.setScale(2, RoundingMode.UNNECESSARY);
  }

private static BigDecimal toPositiveMoney(BigDecimal amount)throws InvalidAmountException {
  
    BigDecimal money = toMoney(amount);
    if (money.signum() <= 0) {
      throw new InvalidAmountException("Amount must be more than zero");
    }
    return money;
  }
}










