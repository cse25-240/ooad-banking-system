package bank.controller;
import java.util.List;
import java.util.regex.Pattern;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.UserDAO;
import bank.dto.CustomerSummary;
import bank.dto.SessionDetails;
import bank.exception.BankingException;
import bank.exception.DeniedAccessException;
import bank.exception.validationException;
import bank.model.Customer;
import bank.model.Role;
import bank.model.User;
import bank.model.companyCustomer;
import bank.model.individualCustomer;

public class CustomerController {

   private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{3,40}");
   private static final int MIN_PASSWORD_LENGTH = 8 ;


    private String newId() {
        return ids.nextCustomerId(id -> customers.findById(id).isPresent());
    }


   private final CustomerDAO customers; 
   private final UserDAO users;
   private final Session session;
   private final IdGenerator ids;

  public CustomerController(CustomerDAO customers, AccountDAO accounts, UserDAO users, Session session, IdGenerator ids){
    this.users = users;
    this.customers = customers;
    this.session = session;
    this.ids = ids;

  } 

  public CustomerSummary registerIndividual(String firstName, String surname, String omang, String address, String phone) throws BankingException{

   session.requireStaff();
   requireNewIdentity(omang);
   try{
    Customer customer = new individualCustomer(newId(),surname, phone, address, firstName, omang);
    customers.save(customer);
    return Mappers.toSummary(customer);
   } catch (IllegalArgumentException e) {
    throw new validationException(e.getMessage());
   }
  }

  public CustomerSummary registerCompany(String companyName, String registrationNumber, String contactOfiicer, String address, String phoneNumber) throws BankingException{
    session.requireStaff();
    requireNewIdentity(registrationNumber);
    try {
        Customer customer = new companyCustomer(newId(), phoneNumber, address, companyName, registrationNumber, contactOfiicer);
        customers.save(customer);
        return Mappers.toSummary(customer);
    } catch (IllegalArgumentException e) {
      throw new validationException(e.getMessage());
    }
  }


  public List<CustomerSummary>list() throws BankingException{
    SessionDetails who =session.requireLogin();
    if (who.staffMember()){
     return customers.findAll().stream().map(Mappers::toSummary).toList();   
    }
    if (who.customerId()==null){
        throw new DeniedAccessException("This login is not associated with a customer");
    }
    return customers.findById(who.customerId()).map(Mappers::toSummary).stream().toList();
  }

  public CustomerSummary find(String customerId ) throws BankingException{
    session.requireStaffOrOwner(customerId);
    return customers.findById(customerId).map(Mappers::toSummary).orElseThrow(()-> new validationException("Customer not found"));
  }

  public CustomerSummary updateDetails( String customerId, String address, String phoneNumber) throws BankingException{
    session.requireStaffOrOwner(customerId);
    Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new validationException("Customer not found."));
        try {
            customer.updateDetails(address, phoneNumber);
        } catch (IllegalArgumentException e) {
            throw new validationException(e.getMessage());
        }
        customers.updateDetails(customer);
        return Mappers.toSummary(customer);
    }


    public void createLogin(String customerId, String username, String password) throws BankingException{
      session.requireStaff();
      if (customers.findById(customerId).isEmpty()) {
        throw new validationException("username not found");
      }  
      if (username==null || !USERNAME.matcher(username.trim()).matches()){
        throw new validationException("username must be 3 to 40 letters with special characters");
      }
      if (password == null || password.length() < MIN_PASSWORD_LENGTH){
         throw new validationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
      }
      if  (users.findByUsername(username).isPresent()) {
          throw new validationException("That username is already taken.");
      }
      String userId = "USR-C" + customerId.substring(customerId.indexOf('-') + 1);
      users.save(new User(userId, username.trim(), password, Role.CUSTOMER, customerId));
    }

 
     private void requireNewIdentity(String omang) throws validationException{
        if (omang != null && customers.findByIdentityNumber(omang).isPresent()){
            throw new validationException("A customer with that ID or registration number already exists.");
        }
     }

  }





