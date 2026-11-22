/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bank.controller;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import bank.dao.AccountDAO;
import bank.dto.InterestSummary;
import bank.exception.BankingException;
import bank.exception.validationException;
import bank.model.Account;
import bank.model.InterestBearing;
/**
 *
 * @author codespace
 */
public class InterestController {

    private final AccountDAO accounts;
    private final Session session;
    private final Clock clock;

    public InterestController(AccountDAO accounts, Session session){
        this(accounts,session,Clock.systemDefaultZone());

    }
    public InterestController(AccountDAO accounts, Session session, Clock clock){
        this.accounts=accounts;
        this.session=session;
        this.clock=clock;
    }



    public InterestSummary run(YearMonth month) throws BankingException {
     session.requireStaff();
     if (month == null) {
     throw new validationException("Choose a month.");
    }
    if (month.isAfter(YearMonth.now(clock))) {
            throw new validationException("Interest cannot be paid for a month that has not started yet.");

    }
    return payFor(month);
    }

    synchronized InterestSummary payFor(YearMonth month){
    List<Account> paid = new ArrayList<>();
    int alreadyPaid = 0;
     BigDecimal total = BigDecimal.ZERO;
     for (Account account : accounts.findAll()) {
     if (account instanceof InterestBearing earner) {
     BigDecimal before = account.getBalance();
        if (earner.InterestPayment(month)) {
         total = total.add(account.getBalance().subtract(before));
             paid.add(account);
               } else {
                alreadyPaid++;
              }
         }
     }
if (!paid.isEmpty()) {
            accounts.updateAll(paid);
        }
        return new InterestSummary(month, paid.size(), alreadyPaid, total);

}
}