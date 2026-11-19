package bank.dao;
import java.util.Optional;

import bank.model.User;
import java.util.List;


public interface UserDAO {

    void save(User user);


    Optional<User> findByUsername(String username);

    Optional<User> findById(String userId);

    List<User> findAll();

    void updateDetails(User user);

    void delete(String userId);


}
