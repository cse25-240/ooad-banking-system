package bank.dao.jdbc;

import bank.dao.AccessDataException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/** */
public class DatabaseManager{

    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager(String url, String user, String password) {
        this.url=url;
        this.user=user;
        this.password=password;
    }

    public static DatabaseManager file(String route) {
        return new DatabaseManager("jdbc:h2:file"+route,"sa","" );
    }

    public static DatabaseManager withinMemory() {
        return new DatabaseManager("jdbc:h2:mem:bank"+ UUID.randomUUID().toString().replace("-", "")
                +";DB_CLOSE_DELAY=-1", "sa", "");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void buildSchema(){
        String sql;
        try (InputStream in = DatabaseManager.class.getResourceAsStream("/schema.sql")) {
            if (in==null){
                throw new AccessDataException("Schema.sql was not located in classpath.");
            }
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e) {
            throw new AccessDataException("schema.sql could not be read",e);
        }

        StringBuilder noComms =new StringBuilder();
        for (String line : sql.split("\\R")){
            if (!line.trim().startsWith("--")){
                noComms.append(line).append('\n');
            }
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()){
            for (String command : noComms.toString().split(";")){
                if(!command.isBlank()){
                    statement.execute(command.trim());
                }
            }
        } catch (SQLException e){
            throw new AccessDataException("Database tables could not be created: " + e.getMessage(), e);
        }
    }

}