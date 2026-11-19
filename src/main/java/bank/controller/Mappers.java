package bank.controller;
import bank.dto.AccountSummary;
import bank.dto.CustomerSummary;
import bank.dto.TransactionRow;
import bank.model.Account;
import bank.model.Customer;
import bank.model.InterestBearing;
import bank.model.Transaction;
import bank.model.Withdrawable;
final class Mappers {

  private Mappers() {
    }

    static CustomerSummary toSummary(Customer c) {
        return new CustomerSummary(c.getCustomerId(), c.getCustomerType(), c.getName(),
                c.getIdentificationNumber(), c.getAddress(), c.getPhoneNumber(), c.getAccounts().size());
    }

    static AccountSummary toSummary(Account a, String ownerId) {
        return new AccountSummary(a.getAccountNumber(), a.getAccountType(), a.getBranchName(), a.getBalance(),
                a.getDateOpened(), ownerId, a instanceof Withdrawable, a instanceof InterestBearing);
    }

    static TransactionRow toRow(Transaction t) {
        return new TransactionRow(t.getTransactionId(), t.getTimestamp(), t.getType().name(),
                t.getAmount(), t.getBalanceAfterTransaction(), t.getDescription());
    }
  

}
