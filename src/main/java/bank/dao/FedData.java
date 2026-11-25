package bank.dao;

import bank.exception.BankingException;
import bank.model.Account;
import bank.model.chequeAccount;
import bank.model.companyCustomer;
import bank.model.Customer;
import bank.model.Employer;
import bank.model.individualCustomer;
import bank.model.investmentAccount;
import bank.model.Role;
import bank.model.savingAccount;
import bank.model.User;

import java.math.BigDecimal;


public class FedData {

 public static final String TELLER_USERNAME = "teller";
 public static final String TELLER_PASSWORD = "Teller*1234";
 public static final String CUSTOMER_PASSWORD = "Customer*1234";

 private static final String BRANCH_GAME = "Game City Gaborone ";
 private static final String BRANCH_NORTH = "Francistown";
 private static final String BRANCH_KANYE = "Kanye";
  
 private FedData(){
 }
 public static void initData(CustomerDAO customers, AccountDAO accounts, UserDAO users){
     
    if (users.findByUsername(TELLER_USERNAME).isPresent()){
        return;
    }
    try{
        users.save(new User("USR-000001", TELLER_USERNAME, TELLER_PASSWORD, Role.TELLER));


        Customer zkesa = person("CNUM-000001", "Zkesa", "Mokgosi", "100000011", "Plot 2234, Game City", "71234567");
        savingAccount zkesaSavings = new savingAccount("SAV-100001",  BRANCH_GAME, money("4000.00"));
        zkesaSavings.deposit(money("600.00"),"Salary add-on");
        investmentAccount zkesaInvestment = new investmentAccount("INV-100001", BRANCH_GAME, money("5000.00"));
        zkesaInvestment.withdraw(money("1000.00"));
        chequeAccount zkesaCheque = new chequeAccount("CHQ-100001", BRANCH_KANYE, money("2000.00"),
        new Employer("Kgatlheng Breweries pty(Ltd) ", "Plot 2245, Kanye "));
        store(customers, accounts, users, zkesa, "zkesa.Mokgosi", zkesaSavings, zkesaInvestment, zkesaCheque);

        Customer tshepo = person("CNUM-000002", "Tshepo", "Dikhwa", "100000022", "Plot 2234, Kanye", "71243093");
        savingAccount tshepoSavings = new savingAccount("SAV-100002", BRANCH_KANYE, money("6700.00"));
        chequeAccount tshepoCheque = new chequeAccount("CHQ-100002", BRANCH_GAME, money("4300.00"),
        new Employer("Botswana Power Corporation", "Ginger Road, Gaborone"));
        store(customers, accounts, users, tshepo, "tshepo.Dikhwa", tshepoSavings, tshepoCheque);

        Customer boitumelo = person("CNUM-000003", "Boitumelo", "Phiri", "100000033", "Plot 410, Gaborone", "71000004");
        savingAccount boitumeloSavings = new savingAccount("SAV-100003", BRANCH_NORTH, money("150.00"));
        chequeAccount boitumeloCheque = new chequeAccount("CHQ-100003", BRANCH_NORTH, money("950.00"),
         new Employer("Sunrise Bank", "Fairgrounds, Gaborone"));
        store(customers, accounts, users, boitumelo, "boitumelo.phiri", boitumeloSavings, boitumeloCheque);

        Customer kalahari = company("CNUM-000004", "Kalahari Traders (Pty) Ltd", "BW00001234567", "Thato Molefe",
                    "Unit 4, Broadhurst Industrial, Gaborone", "72000007");
        savingAccount kalahariSavings = new savingAccount("SAV-100004", BRANCH_KANYE, money("25000.00"));
        investmentAccount kalahariInvestment = new investmentAccount("INV-100002", BRANCH_KANYE, money("60000.00"));
        store(customers, accounts, users, kalahari, "kalahari.traders", kalahariSavings, kalahariInvestment);

 
        Customer baobab = company("CNUM-000005", "Baobab Foods", "BW00003456789", "Kitso Dube",
                    "Plot 301, Francistown", "72000009");
        investmentAccount baobabInvestment = new investmentAccount("INV-100003", BRANCH_NORTH, money("45000.00"));
        baobabInvestment.withdraw(money("2000.00"));
        savingAccount baobabSavings = new savingAccount("SAV-100005", BRANCH_NORTH, money("15000.00"));
        store(customers, accounts, users, baobab, "baobab.foods", baobabInvestment, baobabSavings);


        Customer limpopo = company("CNUM-00006", "Limpopo Builders", "BW00004567890", "Onkabetse Moalosi",
                    "Plot 12, Selebi-Phikwe", "72000010");
        savingAccount limpopoSavings = new savingAccount("SAV-100006", BRANCH_NORTH, money("9000.00"));
        store(customers, accounts, users, limpopo, "limpopo.builders", limpopoSavings);


        Customer tumelo = person("CNUM-000007", "Tumelo", "Sebeso", "100000044", "Plot 7, Maun", "71000003");
        investmentAccount tumeloInvestment = new investmentAccount("INV-10000003", BRANCH_GAME, money("10000.00"));
        savingAccount tumeloSavings = new savingAccount("SAV-10000003", BRANCH_GAME, money("300.00"));
        store(customers, accounts, users, tumelo, "tumelo.sebeso", tumeloInvestment, tumeloSavings);
        
    

    } catch (BankingException e){
        throw new IllegalStateException("Could not initialize date, business rule violated: " + e.getMessage(),e);
    }
 }

private static Customer person(String id, String first, String surname, String nationalId, String address, String phone){
    return new individualCustomer(id, address, phone, first, surname, nationalId);


}

private static Customer company(String id, String name, String registrationNo, String contact, String address, String phone){
    return new companyCustomer(id, address, phone, name , registrationNo, contact);
}

private static void store(CustomerDAO customers, AccountDAO accounts, UserDAO users,  Customer customer, String username, Account... heldAccounts) throws BankingException{

    customers.save(customer);
    for (Account account: heldAccounts){
        customer.addAccount(account);
        accounts.save(customer.getCustomerId(), account);
    }
     String userId = "USR-C" + customer.getCustomerId().substring(4);
        users.save(new User(userId, username, CUSTOMER_PASSWORD, Role.CUSTOMER, customer.getCustomerId()));
} 

private static BigDecimal money(String amount){
    return new BigDecimal(amount);

}
}