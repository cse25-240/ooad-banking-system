/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package bank.controller;
import bank.dao.AccountDAO;
import bank.dto.AccountSummary;
import bank.dto.TransactionRow;
import bank.exception.BankingException;
import bank.exception.validationException;
import bank.model.Account;
import bank.model.Withdrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author codespace
 */
public class TransactionController {
 
    private final AccountDAO accounts;

    private final Session session;

    private AccountSummary summary(Account account) {
        return Mappers.toSummary(account, accounts.findOwnerId(account.getAccountNumber()).orElse(null));
    }



    
public TransactionController(AccountDAO accounts, Session session){
    this.accounts=accounts;
    this.session=session;
}

public AccountSummary deposit(String accountNumber, String amountText, String description)
     throws BankingException {
    Account account = AccountController.visibleAccount(accountNumber, accounts, session);
    var amount = Amounts.parse(amountText);
    if (description == null || description.isBlank()) {
      account.deposit(amount);
  } else {
     account.deposit(amount, description.trim());
        }
        accounts.update(account);
        return summary(account);
    }

    public AccountSummary withdraw(String accountNumber, String amountText) throws BankingException{
      Account account = AccountController.visibleAccount(accountNumber, accounts, session);
      if (!(account instanceof Withdrawable withdrawable)){
       throw new validationException(account.getAccountType() + " accounts do not allow withdrawals."); 
      } 
      withdrawable.withdraw(Amounts.parse(amountText));
      accounts.update(account); 
      return summary(account);
    }


    public List<TransactionRow> history(String accountNumber) throws BankingException {
     Account account = AccountController.visibleAccount(accountNumber, accounts, session);
     List<TransactionRow> rows = new ArrayList<>(account.getTransactions().stream().map(Mappers::toRow).toList());
     Collections.reverse(rows);
     return rows;
    }



}
