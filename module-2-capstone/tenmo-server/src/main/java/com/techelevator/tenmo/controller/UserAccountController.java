package com.techelevator.tenmo.controller;

import java.util.List;

import com.sun.net.httpserver.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserAccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.services.ServerMessage;
import com.techelevator.tenmo.services.TransferMoney;
import com.techelevator.tenmo.services.UserAccountService;

@RestController
public class UserAccountController {

    private final UserAccountDao userAccount;
    private final UserDao userDao;

    public UserAccountController(UserAccountDao userAccountDao, UserDao userDao) {
        this.userAccount = userAccountDao;
        this.userDao = userDao;
    }


    //checking if the user is logged in or not
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    //for check balance
    @GetMapping("/viewBalance")
    public ResponseEntity<?> balanceCheck(){
        if(!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error","user is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId= userDao.findIdByUsername(authentication.getName());

        UserAccountService userBalance = userAccount.getUserBalance(userId);
        return ResponseEntity.ok(userBalance);
    }


    //get All the valid Accounts
    @GetMapping("/accounts")
    public ResponseEntity<?> allAccounts(){
        if(!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error","user is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId= userDao.findIdByUsername(authentication.getName());

        List<UserAccountService> allValidAccounts = userAccount.getAllValidAccounts(userId);

        return ResponseEntity.ok(allValidAccounts);
    }



    //send money to another user
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(
            @RequestParam("recipientId") int recipientId,
            @RequestParam("amount") double amount
    ){
        if(!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error","user is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId= userDao.findIdByUsername(authentication.getName());

        UserAccountService user = userAccount.getUserBalance(userId);

        //validation part

        //checking for if the recipient is valid or not
        UserAccountService recipient = userAccount.getAccountByUserId(recipientId);
        if(recipient == null) {
            return new ResponseEntity<>(new ServerMessage("error","Invalid recipient!"), HttpStatus.BAD_REQUEST);
        }

        //if the user is trying to send to their own account
        if(recipientId == userId) {
            return new ResponseEntity<>(new ServerMessage("error","You can not send money to yourself"), HttpStatus.BAD_REQUEST);
        }

        //minimum sending amount is 10
        if(amount < 10) {
            return new ResponseEntity<>(new ServerMessage("error","Minimum amount is 10"), HttpStatus.BAD_REQUEST);
        }

        if(user.getBalance() < amount) {
            System.out.println("User balance is " + user.getBalance());
            return new ResponseEntity<>(new ServerMessage("error","You don't have sufficient balance!"), HttpStatus.BAD_REQUEST);
        }

        //transferring the money
        TransferMoney transferMoney = userAccount.transferMoney(recipient.getAccountNo(), user.getAccountNo(), amount);
        if(transferMoney == null) {
            return new ResponseEntity<>(new ServerMessage("error","Something went wrong in transferring money"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            String text = String.format("$%.2f Bucks are sent from your account to UserId: %d, AC: %s.%nYour Current Balance is: $%.2f",
                    transferMoney.getAmount(), transferMoney.getReceiverAccount().getUserId(),
                    transferMoney.getReceiverAccount().getAccountNo(), (user.getBalance() - amount));
            return new ResponseEntity<>(new ServerMessage("success",text), HttpStatus.OK);
        }

    }





    //for showing transfer log
    @GetMapping("/transfers")
    public ResponseEntity<?> showTransferLog() {
        if (!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error", "user is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = userDao.findIdByUsername(authentication.getName());


        List<TransferMoney> transactionLog = userAccount.transactionLog(userId,2);

        return ResponseEntity.ok(transactionLog);
    }


    @GetMapping("/pending")
    public ResponseEntity<?> showPendingLog() {
        if (!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error", "user is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = userDao.findIdByUsername(authentication.getName());


        List<TransferMoney> transactionLog = userAccount.transactionLog(userId,1);

        return ResponseEntity.ok(transactionLog);
    }


    @GetMapping("/allTransfers")
    public ResponseEntity<?> showTransaction(){
        if (!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error", "user is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<TransferMoney> transferMoney = userAccount.getTransaction();
        if(transferMoney == null) {
            return new ResponseEntity<>(new ServerMessage("error","Unable to Find transactions"), HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            //String text = transferMoney.getAmount()+" Bucks are sent from your account to UserId:" + transferMoney.getReceiverAccount().getUserId() + ", AC: "+ transferMoney.getReceiverAccount().getAccountNo()+". "
              //      + "Your Current Balance is : " + (user.getBalance() - amount);
            String end = "success";
            return ResponseEntity.ok(transferMoney);//return new ResponseEntity<>(new ServerMessage("success",end), HttpStatus.OK);
        }
        /*
            @GetMapping("/transfers")
    public ResponseEntity<?> showTransferLog() {
        if (!isLoggedIn()) {
            return new ResponseEntity<>(new ServerMessage("error", "user is not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = userDao.findIdByUsername(authentication.getName());


        List<TransferMoney> transactionLog = userAccount.transactionLog(userI d,2);

        return ResponseEntity.ok(transactionLog);
    }
         */


    }



}