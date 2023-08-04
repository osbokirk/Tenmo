package com.techelevator.tenmo.services;

public class UserAccount {

    private UserPayload user;
    private int accountNo;
    private double balance;
    private long userId;

    public UserAccount() {
        // TODO Auto-generated constructor stub
    }




    public UserAccount(int accountNo, double balance, long userId, UserPayload user) {
        super();
        this.accountNo = accountNo;
        this.balance = balance;
        this.userId = userId;
        this.user = user;
    }

    public UserPayload getUser() {
        return user;
    }




    public void setUser(UserPayload user) {
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
    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }



}
