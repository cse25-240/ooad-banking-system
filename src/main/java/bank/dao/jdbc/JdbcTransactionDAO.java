package bank.dao.jdbc;
import bank.dao.AccessDataException;
import bank.dao.TransactionDAO;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JdbcTransactionDAO implements TransactionDAO {

    private static final String INSERT =
            "INSERT INTO transactions (transaction_id,account_number, transaction_type, amount, balance_after, transaction_date, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

private static final String SELECT = "SELECT transaction_id, transaction_type, amount, balance_after, transaction_date, description FROM transactions "; 


private final DatabaseManager db;

public JdbcTransactionDAO(DatabaseManager db){
    this.db=db;
}

@Override 
public void save(String accountNumber, Transaction transaction ) {
    jdbc.write(db, connection -> insert(connection, accountNumber, transaction));
}

@Override 
public Optional<Transaction> findById(String transactionId){
    return jdbc.read(db, connection -> {
        try (PreparedStatement statement = connection.prepareStatement(SELECT + "WHERE transactionId = ?")) {
            statement.setString(1, transactionId);
            try (ResultSet rs = statement.executeQuery()){
                if (rs.next()){
                    return Optional.of(map(rs));
                }
                return Optional.<Transaction>empty();
            }
        }
    });
}

@Override 
public List<Transaction> findByAccount(String accountNumber){
    return jdbc.read(db, connection -> load(connection, accountNumber));
}

static void insert(Connection connection, String accountNumber, Transaction transaction) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement(INSERT)){
        statement.setString(1, transaction.getTransactionId());
        statement.setString(2, accountNumber);
        statement.setString(3, transaction.getType().name());
        statement.setBigDecimal(4, transaction.getAmount());
        statement.setBigDecimal(5, transaction.getBalanceAfterTransaction());
        statement.setTimestamp(6, Timestamp.valueOf(transaction.getTimestamp()));
        statement.setString(7, transaction.getDescription());
        statement.executeUpdate();
    }




}
 
static List<Transaction> load(Connection connection, String accountNumber) throws SQLException {
    List<Transaction> results = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(SELECT + "WHERE account_number = ? ORDER BY seq")) {
        statement.setString(1, accountNumber);
        try (ResultSet rs = statement.executeQuery()){
            while (rs.next()){
                results.add(map(rs));
            }
        }
    }
    return results;
}


 private static Transaction map(ResultSet rs) throws SQLException {
        String type = rs.getString("transaction_type");
        try {
            return new Transaction(
            rs.getString("transaction_id"),
            rs.getBigDecimal("amount"),
            rs.getTimestamp("transaction_date").toLocalDateTime(),
            rs.getString("description"),
            rs.getBigDecimal("balance_after"),
            TransactionType.valueOf(type));
            
        } catch (IllegalArgumentException e) {
            throw new AccessDataException("Unknown transaction type in the database: " + type, e);
        }
    }




}
