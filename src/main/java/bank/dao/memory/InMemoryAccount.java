package bank.dao.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bank.dao.AccessDataException;
import bank.dao.AccountDAO;
import bank.model.Account;



public class InMemoryAccount implements AccountDAO {

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<String, String> owners = new LinkedHashMap<>();

    @Override

    public void save(String customerId, Account account){
        if (accounts.containsKey(account.getAccountNumber())){
            throw new AccessDataException("Account: " + account.getAccountNumber() + " already exists");

        }
        accounts.put(account.getAccountNumber(), account);
        owners.put(account.getAccountNumber(), customerId);
    }

    @Override
    public Optional<Account> findByNumber(String accountNumber){
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override 
    public Optional<String> findOwnerId(String accountNumber){
        return Optional.ofNullable(owners.get(accountNumber));
    }

    @Override
    public List<Account> findByCustomer(String customerId) {
     List<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
         if (customerId.equals(owners.get(account.getAccountNumber()))) {
            result.add(account);
         }
        }
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;
    }

    @Override 
    
    public List<Account> findAll() {
        List<Account> all = new ArrayList<>(accounts.values());
        all.sort(Comparator.comparing(Account::getAccountNumber));
        return all;
    }

    @Override 
    public void update(Account account) {
        updateAll(List.of(account));
    }

     @Override
    public void updateAll(Collection<? extends Account> toUpdate) {
    for (Account account : toUpdate) {
     if (!accounts.containsKey(account.getAccountNumber())) {
        throw new AccessDataException("Account " + account.getAccountNumber() + " does not exist.");
     }
   }
    for (Account account : toUpdate) {
         accounts.put(account.getAccountNumber(), account);
    }
  }

  @Override 
  public void delete(String accountNumber){
    accounts.remove(accountNumber);
    owners.remove(accountNumber);
  }


}
