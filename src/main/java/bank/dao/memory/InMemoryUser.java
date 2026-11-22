package bank.dao.memory;
import bank.dao.AccessDataException;
import bank.dao.UserDAO;
import bank.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public class InMemoryUser implements UserDAO{

    private final Map<String, User> byId = new LinkedHashMap<>();

    @Override 
    public void save(User user) {
     if (byId.containsKey(user.getUserId()) || findByUsername(user.getUsername()).isPresent()) {
       throw new AccessDataException("User " + user.getUsername() + " already exists.");
     }
      byId.put(user.getUserId(), user);
  }

  @Override 
  public Optional<User> findById(String userId ){
    return Optional.ofNullable(byId.get(userId));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    String normalizedUsername = username.toLowerCase(Locale.ROOT);
    return byId.values().stream()
        .filter(user -> user.getUsername().toLowerCase(Locale.ROOT).equals(normalizedUsername))
        .findFirst();
  }

  @Override
  public List<User> findAll(){
    List<User> all = new ArrayList<>(byId.values());
    all.sort(Comparator.comparing(User::getUserId));
    return all;

  }

  @Override 
  public void updateDetails(User user){
    if (!byId.containsKey(user.getUserId())){
        throw new AccessDataException("User: " + user.getUsername()+ " does not exist");

    }
    byId.put(user.getUserId(), user);
  }

  @Override public void delete(String userId){
    byId.remove(userId);
  }

}
