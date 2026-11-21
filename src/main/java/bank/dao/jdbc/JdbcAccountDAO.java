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
                       YearMonth lastPaid, String employerName, String employerAddress) {
    } 


    @Override
    public void save(String customerId, Account account){
        jdbc.write(db, connection -> {
            String sql = "INSERT INTO accounts (account_number, account_type, branch_name, opening_date, " +
                    "balance, last_interest_paid, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)){

                ps.SetString(1, account.getAccountNumber());
                ps.SetString(2, account.getAccountType().toUpperCase());
                ps.SetString(3, customerId);
                ps.SetString(4, account.getBranchName());
                ps.SetDate(5, Date.valueOf(account.getDateOpened()));
                ps.SetBigDecimal(6, account.getBalance());
                ps.SetString(7, account.lastInterestpaid(account));
                if (account instanceof chequeAccount){
                    chequeAccount cheque = (chequeAccount) account;
                    ps.SetString(8, cheque.getEmployer().getName());
                    ps.SetString(9, cheque.getEmployer().getAddress());
                }else{
                    ps.SetNull(8, Types.VARCHAR);
                    ps.SetNull(9, Types.VARCHAR);
                }
                ps.executeUpdate();
            }
            storeNewTransactions(connection, account);
        });
    }


}
