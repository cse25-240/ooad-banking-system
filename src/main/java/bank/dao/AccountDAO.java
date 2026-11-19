package bank.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import bank.model.Account;



/**CRUD principles for reading and loading accounts, any unstored transactions are stored when 
 * an account is saved or updated. a balance will always align with the history**/
public interface AccountDAO {


    
    /** Stores new accounts for existing customers with all its transactions(Create) */

    void save(String customerId, Account account);


   /** Reads one account with its transaction history */

   Optional<Account> findByNumber(String accountNumber);

/** reads account holders' id's **/

   Optional<String> findOwnerId(String accountNumber);

   /**Reads all asingle customers accounts */

   List<Account> findByCustomer(String customerId);

   /**Read every account */

   List<Account> findAll();

   /**(Update) stores the new balance and new transactions of an account */
   void update(Account account);


   void updateAll(Collection<? extends Account> accounts);

   void delete(String accountNumber);



   

}
