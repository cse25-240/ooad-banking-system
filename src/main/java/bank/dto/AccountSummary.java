package bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountSummary(String accountNumber, String accountType, String branch, BigDecimal balance,
    LocalDate openedOn, String ownerId, boolean canWithdraw, boolean earnsInterest) {

 @Override 
 public String toString(){
    return accountNumber + "-" + accountType + "(BWP" + balance.toPlainString() +")";
 }       
    
}
