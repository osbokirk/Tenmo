package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;

public class TransferMoney {

    private int transferId;
    private String transferType;
    private String pendingStatus;
    private UserAccountService senderAccount;
    private UserAccountService receiverAccount;
    private User receiver;
    private double amount;

    public TransferMoney(){

    }

    public int getTransferId() {
        return transferId;
    }
    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }
    public String getTransferType() {
        return transferType;
    }
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
    public String getPendingStatus() {
        return pendingStatus;
    }
    public void setPendingStatus(String pendingStatus) {
        this.pendingStatus = pendingStatus;
    }


    public UserAccountService getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(UserAccountService senderAccount) {
        this.senderAccount = senderAccount;
    }

    public UserAccountService getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(UserAccountService receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }



}
