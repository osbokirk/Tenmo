package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.TransferMoney;
import com.techelevator.tenmo.services.UserAccountService;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class JdbcUserAccountDao implements UserAccountDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //GET account info by account id
    public UserAccountService getAccountInfoById(int accountId) {
        UserAccountService ubp = null;
        String sql = "SELECT * FROM account WHERE account_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if (rowSet.next()) {
            ubp = mapRowToUserBalance(rowSet);
        }
        return ubp;
    }


    //get user balance
    @Override
    public UserAccountService getUserBalance(int userId) {
        UserAccountService ubp = null;
        String sql = "SELECT * FROM account WHERE user_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            ubp = mapRowToUserBalance(rowSet);
        }
        return ubp;
    }


    //get account by user id
    public UserAccountService getAccountByUserId(int userId) {
        UserAccountService ubp = null;
        String sql = "SELECT * FROM account WHERE user_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()) {
            ubp = mapRowToUserBalance(rowSet);
            ubp.setBalance(0);
        }
        return ubp;
    }




    //get all valid account except the given userid
    public List<UserAccountService> getAllValidAccounts(int userId){

        List<UserAccountService> allValidAccounts = new ArrayList<>();

        String recipientAccountSql = "SELECT * FROM account WHERE user_id != ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(recipientAccountSql, userId);


        while (rowSet.next()) {
            UserAccountService userAccount = mapRowToUserBalance(rowSet);
            User user = null;

            //for getting the username
            String recipientUserNameSql = "SELECT * FROM tenmo_user WHERE user_id = ?";
            SqlRowSet rowSetUser = jdbcTemplate.queryForRowSet(recipientUserNameSql, userAccount.getUserId());
            if(rowSetUser.next()) {
                user = mapRowToUser(rowSetUser);
            }
            userAccount.setUser(user);
            userAccount.setBalance(Double.NaN);
            allValidAccounts.add(userAccount);
        }
        return allValidAccounts;
    }




    //transferring money
    @Override
    public TransferMoney transferMoney(long recipientAccountNo, long senderAccountNo, double amount) {

        TransferMoney transferMoney = new TransferMoney();

        int TRANSFER_STATUS_ID = 2;
        int TRANSFER_TYPE_ID = 2;

        String transfer_status = "";
        String transfer_type = "";
        if(TRANSFER_TYPE_ID == 1) {
            transfer_type = "Request";
        }else {
            transfer_type = "Send";
        }

        if(TRANSFER_STATUS_ID == 1) {
            transfer_status = "Pending";
        }else if(TRANSFER_STATUS_ID == 2) {
            transfer_status = "Approved";
        }else if(TRANSFER_STATUS_ID == 3) {
            transfer_status = "Rejected";
        }


        String sql = "INSERT INTO public.transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES ( ?, ?, ?, ?, ?);";
        //inserting into transfer money
        int isTransferred;
        isTransferred = jdbcTemplate.update(sql, TRANSFER_TYPE_ID,TRANSFER_STATUS_ID,senderAccountNo, recipientAccountNo, amount);

        if(isTransferred != 1) {
            return transferMoney;
        }

        //reducing the senders account balance
        boolean reduceSenderMoney = reduceMoney(senderAccountNo, amount);
        //increasing the receiver balance
        boolean increaseRecipientMoney = increaseMoney(recipientAccountNo, amount);

        if(!reduceSenderMoney && !increaseRecipientMoney) {
            return null;
        }

        UserAccountService receiverAccount = null;
        receiverAccount = this.getAccountInfoById((int)recipientAccountNo);

        UserAccountService senderAccount = new UserAccountService();
        receiverAccount.setAccountNo(senderAccountNo);


        transferMoney.setAmount(amount);
        transferMoney.setReceiverAccount(receiverAccount);
        transferMoney.setSenderAccount(senderAccount);
        transferMoney.setTransferType(transfer_type);
        transferMoney.setPendingStatus(transfer_status);


        return transferMoney;
    }


    //reduce Sender Money
    private boolean reduceMoney(long accountId, double amount) {
        UserAccountService uap = null;

        String senderAccountSql = "SELECT * FROM account WHERE account_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(senderAccountSql, accountId);
        if (rowSet.next()) {
            uap = mapRowToUserBalance(rowSet);
        }
        double newBalance = uap.getBalance() - amount;
        String insertNewBalance = "UPDATE account SET balance=? WHERE account_id=?;";
        int update = jdbcTemplate.update(insertNewBalance, newBalance, accountId);

        if(update == 1) {
            return true;
        }else {
            return false;
        }
    }



    //Increase Recipient Money
    private boolean increaseMoney(long accountId, double amount) {

        UserAccountService uap = null;

        String recipientAccountSql = "SELECT * FROM account WHERE account_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(recipientAccountSql, accountId);
        if (rowSet.next()) {
            uap = mapRowToUserBalance(rowSet);
        }

        double newBalance = uap.getBalance() + amount;

        String insertNewBalance = "UPDATE account SET balance=? WHERE account_id=?;";
        int update = jdbcTemplate.update(insertNewBalance, newBalance, accountId);

        if(update == 1) {
            return true;
        }else {
            return false;
        }
    }


    //Transaction log
    public List<TransferMoney> transactionLog(int userId, int transfer_status_id){
        UserAccountService userAccount = this.getAccountByUserId(userId);
        String sql = "SELECT * FROM transfer WHERE account_from = ? AND transfer_status_id =?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userAccount.getAccountNo(), transfer_status_id);

        List<TransferMoney> transactions = new ArrayList<>();

        while(rowSet.next()) {
            TransferMoney transferMoney = mapRowToTransferMoney(rowSet);
            transferMoney.setSenderAccount(null);
            transactions.add(transferMoney);
        }
        return transactions;
    }





    //balance maprow
    private UserAccountService mapRowToUserBalance(SqlRowSet rs) {
        UserAccountService userBalance = new UserAccountService();
        userBalance.setAccountNo(rs.getLong("account_id"));
        userBalance.setBalance(rs.getDouble("balance"));
        userBalance.setUserId(rs.getLong("user_id"));
        return userBalance;
    }

    //user Maprow
    public User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        return user;
    }



    //transfer map row
    private TransferMoney mapRowToTransferMoney(SqlRowSet rs) {
        TransferMoney tm = new TransferMoney();


        int TRANSFER_TYPE_ID =rs.getInt("transfer_type_id");
        int TRANSFER_STATUS_ID = rs.getInt("transfer_status_id");

        String transfer_status = "";
        String transfer_type = "";
        if(TRANSFER_TYPE_ID == 1) {
            transfer_type = "Request";
        }else {
            transfer_type = "Send";
        }

        if(TRANSFER_STATUS_ID == 1) {
            transfer_status = "Pending";
        }else if(TRANSFER_STATUS_ID == 2) {
            transfer_status = "Approved";
        }else if(TRANSFER_STATUS_ID == 3) {
            transfer_status = "Rejected";
        }


        UserAccountService senderAccount = this.getAccountInfoById(rs.getInt("account_from"));
        UserAccountService receiverAccount = this.getAccountInfoById(rs.getInt("account_to"));
        receiverAccount.setBalance(Double.NaN);

        tm.setTransferId(rs.getInt("transfer_id"));
        tm.setTransferType(transfer_type);
        tm.setPendingStatus(transfer_status);
        tm.setSenderAccount(senderAccount);
        tm.setReceiverAccount(receiverAccount);
        tm.setAmount(rs.getDouble("amount"));

        return tm;
    }

    public List<TransferMoney> getTransaction(){
       String Sql = "Select * from Transfer";
       SqlRowSet result = jdbcTemplate.queryForRowSet(Sql);

       List<TransferMoney> oneTransaction = new ArrayList<>();
       while(result.next()){
           TransferMoney temp = mapRowToTransferMoney(result);
           oneTransaction.add(temp);
       }
       return oneTransaction;
    }




}