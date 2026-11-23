package bank.dao.jdbc;

import bank.dao.AccountDAO;
import bank.dao.AccessDataException;
import bank.model.Account;
import bank.model.chequeAccount;
import bank.model.Employer;
import bank.model.InterestBearing;
import bank.model.investmentAccount;
import bank.model.savingAccount;
import bank.model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class JdbcAccountDAO implements AccountDAO {

    private static final String COLUMNS = "account_number, account_type, branch_name, balance, opening_date, last_interest_paid"+ ", employer_name, employer_address ";

    private final DatabaseManager db;

    public JdbcAccountDAO(DatabaseManager db){
        this.db=db;
    }

    private record Rows(String number, String type, String branch, LocalDate openedOn, BigDecimal balance,
                       YearMonth lastInterestPaid, String employerName, String employerAddress) {
    } 


    @Override
    public void save(String customerId, Account account){
        jdbc.write(db, connection -> {
            String sql = "INSERT INTO accounts (account_number, customer_id, account_type, branch_name, opening_date, " +
                    "balance, last_interest_paid, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setString(1, account.getAccountNumber());
                statement.setString(2, customerId);
                statement.setString(3, account.getAccountType().toUpperCase()); 
                statement.setString(4, account.getBranchName());
                statement.setDate(5, Date.valueOf(account.getDateOpened()));
                statement.setBigDecimal(6, account.getBalance());
                statement.setString(7, lastInterestPaid(account));
                if (account instanceof chequeAccount){
                    chequeAccount cheque = (chequeAccount) account;
                    statement.setString(8, cheque.getEmployer().getName());
                    statement.setString(9, cheque.getEmployer().getAddress());
                }else{
                    statement.setNull(8, Types.VARCHAR);
                    statement.setNull(9, Types.VARCHAR);
                }
                statement.executeUpdate();
            }
            storeNewTransactions(connection, account);
        });
    }
/**Reading process */
@Override 
public Optional<Account> findByNumber(String accountNumber){
    return jdbc.read(db, connection -> {
        List<Account> foundAccounts = query(connection, "SELECT " + COLUMNS + " FROM accounts WHERE account_number = ?", accountNumber);
        if (foundAccounts.isEmpty()){
            return Optional.<Account>empty();
        }
        return Optional.of(foundAccounts.get(0));
    } );
}

@Override 
public Optional<String> findOwnerId(String accountNumber){
    return jdbc.read(db, connection -> {
        try (PreparedStatement ps = connection.prepareStatement("SELECT customer_id FROM accounts WHERE account_number =?")){
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(rs.getString(1));
                }
                return Optional.<String>empty();

            }
        }
    });
}

@Override 
public List<Account> findByCustomer(String customerId){
    return jdbc.read(db, connection -> query(connection, "SELECT " + COLUMNS + " FROM accounts WHERE customer_id = ?", customerId));


}

@Override
public List<Account> findAll(){
return jdbc.read(db, connection -> query(connection, "SELECT" + COLUMNS + "FROM accounts ORDER BY account_number", null));
}



/**Updating process */

@Override
public void update(Account account){
    updateAll(List.of(account));
}

@Override 
 public void updateAll(Collection<? extends Account> accounts) {
        jdbc.write(db, connection -> {
            for (Account account : accounts) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "UPDATE accounts SET balance = ?, last_interest_paid = ? WHERE account_number = ?")) {
                    ps.setBigDecimal(1, account.getBalance());
                    ps.setString(2, lastInterestPaid(account));
                    ps.setString(3, account.getAccountNumber());
                    if (ps.executeUpdate() == 0) {
                        throw new AccessDataException("Account " + account.getAccountNumber() + " does not exist.");
                    }
                }
                storeNewTransactions(connection, account);
            }
        });
    }

    @Override
    public void delete(String accountNumber){
        jdbc.write(db, connection -> {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM accounts WHERE account_number = ?")) {
                ps.setString(1, accountNumber);
                ps.executeUpdate();
            }
        });
    }

    private static void storeNewTransactions(Connection connection, Account account) throws SQLException {
        Set<String> storedTransactions = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT transaction_id FROM transactions WHERE account_number = ?")) {
            ps.setString(1, account.getAccountNumber());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    
                    storedTransactions.add(rs.getString(1));
                }
            }
        }
        for (Transaction transaction : account.getTransactions()){
            if (!storedTransactions.contains(transaction.getTransactionId())){
                JdbcTransactionDAO.insert(connection, account.getAccountNumber(), transaction);
            }
        }
    }

     private static List<Account> query(Connection connection, String sql, String parameter) throws SQLException {
        List<Rows> rows = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (parameter != null) {
                ps.setString(1, parameter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String last = rs.getString("last_interest_paid");
                    rows.add(new Rows(
                            rs.getString("account_number"),
                            rs.getString("account_type"),
                            rs.getString("branch"),
                            rs.getDate("opened_on").toLocalDate(),
                            rs.getBigDecimal("balance"),
                            last == null ? null : YearMonth.parse(last),
                            rs.getString("employer_name"),
                            rs.getString("employer_address")));
                }
            }
        }
        List<Account> accounts = new ArrayList<>();
        for (Rows row : rows) {
            accounts.add(build(row, JdbcTransactionDAO.load(connection, row.number())));
        }
        return accounts;
    }

    private static Account build(Rows row, List<Transaction> history) {
        return switch (row.type()){
            case "SAVINGS" -> new savingAccount(row.number(), row.branch(), row.openedOn(), row.balance(), history,
        row.lastInterestPaid());
            case "CHEQUE" -> new chequeAccount(row.number(), row.branch(), row.openedOn(),
                new Employer(row.employerName(), row.employerAddress()), row.balance(), history);
        case "INVESTMENT" -> new investmentAccount(row.number, row.branch(), row.openedOn(), row.balance(), history,
        row.lastInterestPaid());
            default -> throw new AccessDataException("Unknown account type: " + row.type());
        };
    }


    private static String lastInterestPaid(Account account) {
        if (account instanceof InterestBearing interest && interest.getLastInterestCalculation() != null) {
            return interest.getLastInterestCalculation().toString();
        }
        return null;
    }










}