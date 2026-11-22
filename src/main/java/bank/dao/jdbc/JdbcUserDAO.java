package bank.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bank.dao.AccessDataException;
import bank.dao.UserDAO;
import bank.model.Role;
import bank.model.User;


public class JdbcUserDAO implements UserDAO{

    private static final String COLUMNS = "user_id, username, password_hash, role, customer_id, failed_attempts, locked_until";

 
    private final DatabaseManager db;

    public JdbcUserDAO(DatabaseManager db) {
        this.db = db;
    }


    @Override 
    public void save(User user) {
        jdbc.write(db, connection -> {
            String sql = "INSERT INTO users (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getUserId());
                statement.setString(2, user.getUsername());
                statement.setString(3, user.getPasswordHash());
                statement.setString(4, user.getRole().name());
                if (user.getCustomerId()== null) {
                    statement.setNull(5, Types.VARCHAR);
                } else {
                    statement.setString(5, user.getCustomerId());
                }
                statement.setInt(6, user.getFailedLoginAttempts());
                setTime(statement, 7, user.getLockoutEndTime());
                statement.executeUpdate();
            }
        });
    }

     @Override
    public Optional<User> findByUsername(String username) {
        String wanted = username == null ? "" : username.trim();
        return findOne("LOWER(username) = LOWER(?)", wanted);
    }

    @Override
    public Optional<User> findById(String userId) {
        return findOne("user_id = ?", userId);
    }

    @Override
    public List<User> findAll() {
        return jdbc.read(db, connection -> readAll(connection,
                "SELECT " + COLUMNS + " FROM app_users ORDER BY user_id", null));
    }

    private Optional<User> findOne(String condition, String value) {
        List<User> found = jdbc.read(db, connection -> readAll(connection,
                "SELECT " + COLUMNS + " FROM app_users WHERE " + condition, value));
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }


public void updateDetails(User user) {
        jdbc.write(db, connection -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE app_users SET failed_attempts = ?, locked_until = ? WHERE user_id = ?")) {
                ps.setInt(1, user.getFailedLoginAttempts());
                setTime(ps, 2, user.getLockoutEndTime());
                ps.setString(3, user.getUserId());
                if (ps.executeUpdate() == 0) {
                    throw new AccessDataException("User " + user.getUsername() + " does not exist.");
                }
            }
        });
    }



 @Override
    public void delete(String userId) {
        jdbc.write(db, connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM app_users WHERE user_id = ?")) {
                statement.setString(1, userId);
                statement.executeUpdate();
            }
        });
    }




  private static void setTime(PreparedStatement ps, int index, LocalDateTime time) throws SQLException {
        if (time == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(time));
        }
    }

private static List<User> readAll(Connection connection, String sql, String parameter) throws SQLException {
  List<User> result = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        if (parameter != null) {
             ps.setString(1, parameter);
    }
      try (ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
             Timestamp locked = rs.getTimestamp("locked_until");
                result.add(new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("failed_attempts"),
                        locked == null ? null : locked.toLocalDateTime(),
                        rs.getString("customer_id")));
                }
            }
        }
        return result;
    }  










}
