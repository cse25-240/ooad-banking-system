package bank.dao;

import java.util.List;
import java.util.Optional;

import bank.model.Customer;


public interface CustomerDAO {
/** Uses CRUD principles to store and load customers. accounts are stored with a link to this interface 
 * 
 */


//create) stores a new customer
void save(Customer customer);

//read)all a single customers accounts
Optional<Customer> findById(String customerId);

//read)finds customers by their omang numbers 
Optional<Customer> findByIdentityNumber(String omang);

List<Customer>findAll();

//update ) stores modifiable contact details
void updateDetails(Customer customer);

//Delete)erases customers and their logins
void delete(String customerId);


}
