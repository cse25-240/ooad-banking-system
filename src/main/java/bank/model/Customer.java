package bank.model;
import bank.exception.BankingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**abstract base class for individual and company customers 
 * 
 * composition relationship with Account class, a customer can owns its accounts
 * an account cant exist without a customer, through addAccount() method.Only one 
 * account of the same type can be owned by a customer
 */

public abstract class Customer{

    private final String customerId;
    private String phoneNumber;
    private String address;
    private final List<Account> accounts= new ArrayList<>();

    protected Customer(String customerId, String phoneNumber, String address) {
        this.customerId = Employer.requireText(customerId, "Customer ID");
        this.phoneNumber = Employer.requireText(phoneNumber, "Phone Number");
        this.address = Employer.requireText(address, "Address");
    }

    public abstract String getName();

    public abstract String getCustomerType();

    public abstract String getIdentificationNumber();

/**This is overridden by subclasses to check eligibility for opening an account 
 * refuse to add account if not eligible by throwing BankingException
*/
    protected void checkEligibility(Account account) throws BankingException{

    }

    public void addAccount(Account account) throws BankingException{
        if (account==null){
            throw new BankingException("Account Required");
        }
        for (Account exists: accounts){
            if (exists.getAccountNumber().equals(account.getAccountNumber())){
                throw new BankingException("Account with number "+account.getAccountNumber()+" already exists for this customer");
            }
            if (exists.getAccountType().equals(account.getAccountType())) {
                throw new BankingException("Customer Already has a" + account.getAccountType() + "account");
            }
        }
        checkEligibility(account);
        accounts.add(account);
    }
/**Allows customer to update their contact details */
    public void updateDetails(String phoneNumber, String address) {
        String newPhoneNumber= Employer.requireText(phoneNumber, "Phone Number");
        String newAddress= Employer.requireText(address, "Address");
        this.phoneNumber = newPhoneNumber;
        this.address = newAddress;
    }

    public List<Account> getAccounts(){
        return Collections.unmodifiableList(accounts);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
    

}
