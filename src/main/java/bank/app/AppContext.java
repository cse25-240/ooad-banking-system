package bank.app;

import bank.controller.AccountController;
import bank.controller.Controllers;
import bank.controller.CustomerController;
import bank.controller.IdGenerator;
import bank.controller.InterestController;
import bank.controller.LoginController;
import bank.controller.Session;
import bank.controller.TransactionController;
import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.FedData;
import bank.dao.UserDAO;
import bank.dao.jdbc.DatabaseManager;
import bank.dao.jdbc.JdbcAccountDAO;
import bank.dao.jdbc.JdbcCustomerDAO;
import bank.dao.jdbc.JdbcUserDAO;
import bank.dao.memory.InMemoryAccount;
import bank.dao.memory.InMemoryCustomer;
import bank.dao.memory.InMemoryUser;

import java.time.Clock;
public class AppContext {

    private final AppContext() {

 

    }
private static Conntrollers feed (CustomerDAO customers, AccountDAO accounts, UserDAO users){
    Session session = new Session();
    IdGenerator ids = new IdGenerator();
    InterestController interest = new InterestController(accounts, session);
    return new Controllers(session,
           new LoginController(users,session),
           new CustomerController(customers, accounts, users, session, ids),
           new AccountController(customers, accounts, session, ids),
           new TransactionController(accounts, session), interest,
           new InterestScheduler(interest, Clock.systemDefaultZone()));
}




public static Controllers inMemory(boolean withSampleData){
    CustomerDAO customers = new InMemoryCustomer();
    AccountDAO accounts = new InMemoryAccount();
    UserDAO users = new InMemoryUser();
    if (withSampleData) {
        FedData.initData(customers, accounts, users);
    }
    return feed(customers, accounts, users);
}

public static Controllers h2(String filePath) {
    return fromDatabase(DatabaseManager.file(filePath));



}



public static Controllers fromDatabase(DatabaseManager db){
    db.buildSchema();
    AccountDAO accounts = new JdbcAccountDAO(db);
    CustomerDAO customers = new JdbcCustomerDAO(db, accounts);
    UserDAO users = new JdbcUserDAO(db);
    FedData.initData(customers, accounts, users);
    return feed(customers, accounts, users);

}







}
