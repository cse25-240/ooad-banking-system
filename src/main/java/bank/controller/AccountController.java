/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dto.AccountSummary;
import bank.dto.SessionDetails;
import bank.exception.DeniedAccessException;
import bank.exception.BankingException;
import bank.exception.validationException;
import bank.model.Account;
import bank.model.chequeAccount;
import bank.model.Customer;
import bank.model.Employer;
import bank.model.investmentAccount;
import bank.model.savingAccount;

import java.util.List;




/**
 *
 * @author codespace
 */
public class AccountController {

    static final String UNAVAILABLE = "Account is unavailable";

    private final CustomerDAO customers;
    private final AccountDAO accounts;
    private final Session session;
    private final IdGenerator ids;

    
 public AccountController(CustomerDAO customers, AccountDAO accounts, Session session, IdGenerator ids) {
     this.customers = customers;
     this.accounts = accounts;
     this.session = session;
     this.ids = ids;
    }  
    
    public AccountSummary openSaving(String customerId, String branch, String openingDeposit)
    throws BankingException{
    Customer customer = customerForOpening(customerId);
     return open(customer, new savingAccount(number("SAV"), requireBranch(branch), Amounts.parse(openingDeposit)));    
    }

    public AccountSummary openInvestment(String customerId, String branch, String openingDeposit)
            throws BankingException {
        Customer customer = customerForOpening(customerId);
        return open(customer, new investmentAccount(number("INV"), requireBranch(branch), Amounts.parse(openingDeposit)));
    }

    public AccountSummary openCheque(String customerId, String branch, String openingDeposit,
         String employerName, String employerAddress) throws BankingException {
    Customer customer = customerForOpening(customerId);
     try {
        Employer employer = new Employer(employerName, employerAddress);
         return open(customer, new chequeAccount(number("CHQ"), requireBranch(branch),
             Amounts.parse(openingDeposit), employer));
        } catch (IllegalArgumentException e) {
            throw new validationException(e.getMessage());
        }
     }
/** Staff see every account; a customer sees only their own. */
    public List<AccountSummary> list() throws BankingException {
    SessionDetails who = session.requireLogin();
    if (who.staffMember()) {
         return accounts.findAll().stream()
        .map(a -> Mappers.toSummary(a, accounts.findOwnerId(a.getAccountNumber()).orElse(null)))
         .toList();
        }
        return listFor(who.customerId());
}
 public List<AccountSummary> listFor(String customerId) throws BankingException {
     session.requireStaffOrOwner(customerId);
     return accounts.findByCustomer(customerId).stream()
            .map(a -> Mappers.toSummary(a, customerId))
            .toList();
    }

    public AccountSummary find(String accountNumber) throws BankingException {
        Account account = visibleAccount(accountNumber, accounts, session);
        return Mappers.toSummary(account, accounts.findOwnerId(accountNumber).orElse(null));
    }


    /** Loads Accounts signed in users can see and missing or different holder accounts display the same message
     * to prevent peeking and seeing which acc numbers exist
     */

 static Account visibleAccount(String accountNumber, AccountDAO accounts, Session session)
            throws DeniedAccessException {
     SessionDetails who = session.requireLogin();
     Account account = accounts.findByNumber(accountNumber == null ? "" : accountNumber.trim())
        .orElseThrow(() -> new DeniedAccessException(UNAVAILABLE));
     if (!who.staffMember()) {
        String owner = accounts.findOwnerId(account.getAccountNumber()).orElse(null);
        if (owner == null || !owner.equals(who.customerId())) {
            throw new DeniedAccessException (UNAVAILABLE);
         }
        }
        return account;
    }
   
    private Customer customerForOpening(String customerId) throws BankingException{
       session.requireStaff();
       return customers.findById(customerId).orElseThrow(() -> new validationException("Customer not found")); 
    }

    private AccountSummary open(Customer customer, Account account) throws BankingException {
        customer.addAccount(account);          /** this enforces the one of each type, no cheque for companies rule*/ 
        accounts.save(customer.getCustomerId(), account);
        return Mappers.toSummary(account, customer.getCustomerId());
    }

    private String number(String prefix){
      return ids.nextAccountNumber(prefix, n -> accounts.findByNumber(n).isPresent());  
    }

    private static String requireBranch(String branch) throws validationException {
     if (branch == null || branch.isBlank()) {
        throw new validationException("Choose a branch.");
     }
     return branch.trim();
    }





}