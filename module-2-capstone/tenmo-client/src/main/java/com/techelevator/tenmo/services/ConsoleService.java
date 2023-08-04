package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.TransferMoney;
import com.techelevator.tenmo.services.UserAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printValidUsers(List<UserAccount> allAccounts) {
        // Print header
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-10s %-10s%n", "ID", "Name");
        System.out.println("-------------------------------------------");

        // Print data
        for(UserAccount user : allAccounts) {
            System.out.printf("%-10d %-10s%n", user.getUserId(), user.getUser().getUsername());
        }

        // Print footer
        System.out.println("-------------------------------------------");
    }




    public void printAllTransaction(List<TransferMoney> transfers) {
        // Print header
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-15s %-15s %-10s %-10s %-10s%n", "Transfers ID", "From", "Receiver Id", "Receiver AC ", "Amount", "Type", "Status");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");

        // Print data
        for (TransferMoney transfer : transfers) {
            System.out.printf("%-15d %-15s %-15s $%.2f %-10s %-10s%n",
                    transfer.getTransferId(),
                    "You",
                    transfer.getReceiverAccount().getUserId(),
                    " AC",
                    0,
                    transfer.getTransferType(),
                    transfer.getPendingStatus());
        }

        // Print footer
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }


    public void bal(List<TransferMoney> allTransactions) {
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        System.out.println("Transfers ID    From            Receiver Id         Receiver AC        Amount     Type         Status");
        System.out.println("-----------------------------------------------------------------------------------------------------------------");


        for(TransferMoney transfer : allTransactions) {
            int transferId = transfer.getTransferId();
            String from = "You";
            String accountUser = transfer.getReceiverAccount().getUserId()+"";
            String accountNo = transfer.getReceiverAccount().getAccountNo()+"";
            double amount = transfer.getAmount();
            String type = transfer.getTransferType();
            String status = transfer.getPendingStatus();

            System.out.printf("%d            %s              %s                %s             $%.2f      %s         %s\n",
                    transferId, from, accountUser, accountNo,amount, type, status);
        }



        System.out.println("-----------------------------------------------------------------------------------------------------------------");
    }

    public void oneTransaction(TransferMoney theOne, List<UserAccount> userList, AuthenticatedUser credentials) {
        System.out.println("-----------------------------------------------------------------------------------------------------------------\n" +
                "Transfer Details\n" +
                "-----------------------------------------------------------------------------------------------------------------");
        String from = null;
        String to = null;
        for(UserAccount useracct:userList){
            if(useracct.getUserId()== theOne.getSenderAccount().getUserId()){
                from = useracct.getUser().getUsername();
            }if (useracct.getUserId() == theOne.getReceiverAccount().getUserId()) {
                to = useracct.getUser().getUsername();
            }
        }
        if(from == null){
            from = credentials.getUser().getUsername();
        }
        if(to == null){
            to = credentials.getUser().getUsername();
        }
        double amount = theOne.getAmount();
        String type = theOne.getTransferType();
        String status = theOne.getPendingStatus();
        int transferId = theOne.getTransferId();

        System.out.printf("TransferId: %s%nFrom: %s%nTo: %s%nAmount: $%.2f%nType: %s%nStatus: %s%n",
                transferId, from, to, amount, type, status);
        }
    }
