package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.services.TransferMoney;
import com.techelevator.tenmo.services.UserAccountService;

public interface UserAccountDao {

    List<UserAccountService> getAllValidAccounts(int userId);

    UserAccountService getUserBalance(int userId);
    UserAccountService getAccountByUserId(int userId);
    TransferMoney transferMoney(long recipientAccountNo, long userAccountNo, double amount);

    List<TransferMoney> transactionLog(int userId, int transfer_status_id);

    List<TransferMoney> getTransaction();


}
