package bank.controller;

public record Controllers( Session session, LoginController login,AccountController accounts,
                           InterestController interest, TransactionController transactions, CustomerController customers){
                            
                           }