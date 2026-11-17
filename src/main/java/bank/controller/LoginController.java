package bank.controller;

import bank.dao.UserDAO;
import bank.dto.SessionInfo;
import bank.exception.DeniedAccessException;
import bank.exception.BankingException;
import bank.model.User;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

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

public SessionInfo login(String username, String Password)

}