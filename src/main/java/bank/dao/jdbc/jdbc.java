package bank.dao.jdbc;

import bank.dao.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

final class jdbc {

private jdbc(){

}

interface Query<T>{
    T run(Connection connection ) throws SQLException;
}


interface Change {
    void run(Connection connection) throws SQLException;
}

static <T> T read(DatabaseManager db, Query<T> query){
    try (connection connection = db.getConnection()){
        return query.run(connection);
    }catch (SQLException e) {
        throw new AcessDataException("Datase error: "+e.getMessage(), e);
    }
}

static void write(DatabaseManager db, Change change) {
  try (Connection connection = db.getConnection()){
    connection.setAutoCommit(fasle);
    try{
        change.run(connection);
        connection.commit();
    }catch (SQLException | RuntimeException e){
        connection.rollback();
        throw e;
    }
  }catch (SQLException e) {
    throw new AccessDataException("Database error :" + e.getMessage(),e);
  }
}

}