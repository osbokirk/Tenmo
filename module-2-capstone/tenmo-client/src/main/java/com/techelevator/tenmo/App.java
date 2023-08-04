package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.ServerMessage;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserAccount;

import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        authenticationService.checkBalance(currentUser);
        // Send a request to the API to get the current account balance
//        double balance = authenticationService.getBalance(currentUser.getToken());
		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        authenticationService.viewTransferHistory(currentUser);
        List<UserAccount> UserList = authenticationService.getUsers(currentUser);
        authenticationService.getTransaction(currentUser,UserList);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        authenticationService.viewPendingHistory(currentUser);
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        System.out.println("***************************************************");
        System.out.println("	List of All Valid Users");
        System.out.println("***************************************************");
        authenticationService.getAllValidUsers(currentUser);
        System.out.println();

        int userId = consoleService.promptForMenuSelection("Please enter a UserId to send money to: ");
        int amount = consoleService.promptForMenuSelection("Enter the amount: ");

        ServerMessage serverMsg = authenticationService.sendBucks(currentUser, userId, amount);

        if(serverMsg != null) {
            if(serverMsg.getType().equalsIgnoreCase("success")) {
                System.out.println("\nSuccess: "+serverMsg.getMessage());
            }else {
                System.out.println("\nError: "+serverMsg.getMessage());
            }
        }

    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
