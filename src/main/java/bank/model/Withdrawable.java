package bank.model;

import bank.exception.InsufficientFundsException;
import bank.exception.InvalidAmountException;
import java.math.BigDecimal;

public interface Withdrawable {
    void withdraw(BigDecimal amount) throws InsufficientFundsException, InvalidAmountException;
} 
/** used by investment and cheque accounts, savingsAccounts doesn't implement it */