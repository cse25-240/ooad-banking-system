package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID; 

public class Transaction {
    private final String transactionId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;
    private final BigDecimal balanceAfterTransaction;
    private final TransactionType type;

    /** constructor for when the balance changes */
    Transaction(String transactionId, BigDecimal amount, String description,
                      BigDecimal balanceAfterTransaction, TransactionType type) {
        this( UUID.randomUUID().toString(),
                amount, LocalDateTime.now(), description, balanceAfterTransaction, type);
    }

    /** constructor that's full, used by DAO to remake an already existing transaction */
    public Transaction(String transactionId, BigDecimal amount, LocalDateTime timestamp,
                      String description, BigDecimal balanceAfterTransaction, TransactionType type) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.type = type;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
  }
  public String getDescription(){
    return description;
  }
  public BigDecimal getBalanceAfterTransaction(){
    return balanceAfterTransaction;
  }
  public TransactionType getType(){
    return type;
  }

  


}
