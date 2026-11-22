package bank.dao.memory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bank.dao.AccountDAO;
import bank.dao.TransactionDAO;
import bank.model.Account;
import bank.model.Transaction;


public class InMemoryTransaction implements TransactionDAO {

    private final AccountDAO accounts;

    public InMemoryTransaction(AccountDAO accounts){
        this.accounts = accounts;
    }

    @Override 
    public void save(String accountNumber, Transaction transaction){
        
    }

    @Override 
    public Optional<Transaction> findById(String transactionId){
        for (Account account : accounts.findAll()){
            for (Transaction transaction : account.getTransactions()){
                if (transaction.getTransactionId().equals(transactionId)){
                    return Optional.of(transaction);
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public List<Transaction> findByAccount(String accountNumber){
        return accounts.findByNumber(accountNumber).map(acc -> new ArrayList<>(acc.getTransactions()))
                .orElseGet(ArrayList::new);
    }


}
