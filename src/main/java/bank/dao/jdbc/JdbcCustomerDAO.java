package bank.dao.jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bank.dao.AccessDataException;
import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.exception.BankingException;
import bank.model.Account;
import bank.model.Customer;
import bank.model.companyCustomer;
import bank.model.individualCustomer;



public class JdbcCustomerDAO implements CustomerDAO {

    private static final String COLUMNS = "customer_id, customer_type, address, phone, first_name, surname, national_id, company_name, registration_no, contact_person ";

    private final DatabaseManager db;
    private final AccountDAO accounts;

    public JdbcCustomerDAO(DatabaseManager db, AccountDAO accounts) {
        this.db = db;
        this.accounts = accounts;
    }

@Override 
public void save(Customer customer){
    jdbc.write(db, connection -> {
    String sql = "INSERT INTO customers (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement satement = connection.prepareStatement(sql)){
        
        satement.setString(1, customer.getCustomerId());
        satement.setString(2, customer.getCustomerType().toUpperCase());
        satement.setString(3, customer.getAddress());
        satement.setString(4, customer.getPhoneNumber());
        if (customer instanceof individualCustomer){
            individualCustomer individual = (individualCustomer) customer;
            satement.setString(5, individual.getFirstName());
            satement.setString(6, individual.getLastName());
            satement.setString(7, individual.getNationalId());
            satement.setNull(8, Types.VARCHAR);
            satement.setNull(9, Types.VARCHAR);
            satement.setNull(10, Types.VARCHAR);
        } else if (customer instanceof companyCustomer){
            companyCustomer company = (companyCustomer) customer;
            satement.setNull(5, Types.VARCHAR);
            satement.setNull(6, Types.VARCHAR);
            satement.setNull(7, Types.VARCHAR);
            satement.setString(8, company.getCompanyName());
            satement.setString(9, company.getRegistrationNumber());
            satement.setString(10, company.getContactOfficer());
        } else {
            throw new AccessDataException("Unknown customer type: " + customer.getClass().getName());
        }
        satement.executeUpdate();
        
    }    
    });
}


@Override 
 public Optional<Customer> findById(String customerId) {
        return findOne("customer_id = ?", customerId, null);
    }

    @Override
    public Optional<Customer> findByIdentityNumber(String identityNumber) {
        String wanted = identityNumber == null ? "" : identityNumber.trim();
        return findOne("national_id = ? OR registration_no = ?", wanted, wanted);
    }
 
    @Override
    public List<Customer> findAll() {
        List<Customer> shells = jdbc.read(db, connection -> readAll(connection,
                "SELECT " + COLUMNS + " FROM customers ORDER BY customer_id", null, null));
        List<Customer> results = new ArrayList<>();
        for (Customer shell : shells) {
            results.add(withAccounts(shell));
        }
        return results;
    }

    private Optional<Customer> findOne(String condition, String first, String second) {
        List<Customer> shells = jdbc.read(db, connection -> readAll(connection,
                "SELECT " + COLUMNS + " FROM customers WHERE " + condition, first, second));
        if (shells.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(withAccounts(shells.get(0)));
    
    }

    public void updateDetails(Customer customer){
        jdbc.write(db, connection -> {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE customers SET address = ?, phone = ? WHERE customer_id = ?")) {
                
                statement.setString(1, customer.getAddress());
                statement.setString(2, customer.getPhoneNumber());
                statement.setString(3, customer.getCustomerId());
                
                if (statement.executeUpdate() == 0) {
                    throw new AccessDataException("Customer not found: " + customer.getCustomerId());
                }
            }
        });
    }


    @Override 
    public void delete(String customerId){
        if (!accounts.findByCustomer(customerId).isEmpty()){
            throw new AccessDataException("Cannot delete a customer who still has accounts");
        }
        jdbc.write(db, connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM app_users WHERE customer_id = ?")) {
                statement.setString(1, customerId);
                statement.executeUpdate();
            }
        });
    }


private static List<Customer> readAll(Connection connection, String sql, String first, String second)
            throws SQLException {
        List<Customer> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (first != null) {
                ps.setString(1, first);
            }
            if (second != null) {
                ps.setString(2, second);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(toCustomer(rs));
                }
            }
        }
        return result;
    }

    private static Customer toCustomer(ResultSet rs) throws SQLException {
        if ("INDIVIDUAL".equals(rs.getString("customer_type"))) {
            return new individualCustomer(rs.getString("customer_id"), rs.getString("address"),
                    rs.getString("phone"), rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("national_id"));
        }
        return new companyCustomer(rs.getString("customer_id"), rs.getString("address"),
                rs.getString("phone"), rs.getString("company_name"), rs.getString("registration_no"),
                rs.getString("contact_person"));
    }

    /** Attaches the stored accounts to a freshly loaded customer. */
    private Customer withAccounts(Customer customer) {
        for (Account account : accounts.findByCustomer(customer.getCustomerId())) {
            try {
                customer.addAccount(account);
            } catch (BankingException e) {
                throw new AccessDataException("Stored data breaks a business rule: " + e.getMessage(), e);
            }
        }
        return customer;
    }   




}
