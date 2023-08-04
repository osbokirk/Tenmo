package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;

public class UserAccountService {

    private User user;
    private long accountNo;
    private double balance;
    private long userId;

    public UserAccountService() {
        // TODO Auto-generated constructor stub
    }




    public UserAccountService(long accountNo, double balance, long userId, User user) {
        super();
        this.accountNo = accountNo;
        this.balance = balance;
        this.userId = userId;
        this.user = user;
    }

    public User getUser() {
        return user;
    }




    public void setUser(User user) {
        this.user = user;
    }




    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public long getAccountNo() {
        return accountNo;
    }
    public void setAccountNo(long accountNo) {
        this.accountNo = accountNo;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }



}
