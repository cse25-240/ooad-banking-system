package bank.controller;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import bank.dao.UserDAO;
import bank.dto.SessionDetails;
import bank.exception.BankingException;
import bank.exception.DeniedAccessException;
import bank.model.User;

public class LoginController {
    private static final String WRONG_LOGIN= "Invalid username or password.";

    private final UserDAO users;
    private final Session session;
    private final Clock clock;

    public LoginController(UserDAO users,Session session){
        this(users,session,Clock.systemDefaultZone());

    }

public LoginController(UserDAO users, Session session, Clock clock){
    this.users = users;
    this.session = session;
    this.clock = clock;
}

public SessionDetails login(String username, String password) throws BankingException{
    if (username == null || username.isBlank() || password == null || password.isEmpty()){
        throw new DeniedAccessException("Enter your username and password");
    }
    Optional<User> found = users.findByUsername(username);
    /**same message for an unknown user and wrong password to prevent username digging */

    if  (found.isEmpty()){
        throw new DeniedAccessException(WRONG_LOGIN);
    }
    User user = found.get();
    LocalDateTime now = LocalDateTime.now(clock);

    if (user.isLockedOut(now)){
        double minutes = Math.max(1, Duration.between(now, user.getLockoutEndTime()).plusSeconds(59).toMinutes());
        throw new DeniedAccessException("Account is locked out due to many failed attempts. Retry after" 
            + minutes + "minutes " + (minutes==1? "" : "s") + ".");
        
    }

    if (!user.authenticate(password)) {
     user.recordFailedLoginAttempt(now);
     users.updateDetails(user);
     if (user.isLockedOut(now)) {
         throw new DeniedAccessException("Too many failed attempts. This account is locked for "
             + User.LOCKOUT_DURATION.toMinutes() + " minutes.");
        }
            throw new DeniedAccessException(WRONG_LOGIN);
            
}
if (user.getFailedLoginAttempts() > 0) {
     user.resetFailedLoginAttempts();
     users.updateDetails(user);
 }
    SessionDetails info = new SessionDetails(user.getUserId(), user.getUsername(), user.getRole().name(),
        user.getCustomerId());
        session.begin(info);
        return info;
}
    public void logout(){
        session.logout();
    }    

}