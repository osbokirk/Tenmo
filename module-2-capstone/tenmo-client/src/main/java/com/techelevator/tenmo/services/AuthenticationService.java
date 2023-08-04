package com.techelevator.tenmo.services;

import com.techelevator.util.BasicLogger;


import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.ServerMessage;
import com.techelevator.tenmo.services.TransferMoney;
import com.techelevator.tenmo.services.UserAccount;

public class AuthenticationService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticationService(String url) {
        this.baseUrl = url;
    }

    public AuthenticatedUser login(UserCredentials credentials) {
        HttpEntity<UserCredentials> entity = createCredentialsEntity(credentials);
        AuthenticatedUser user = null;
        try {
            ResponseEntity<AuthenticatedUser> response =
                    restTemplate.exchange(baseUrl + "login", HttpMethod.POST, entity, AuthenticatedUser.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public boolean register(UserCredentials credentials) {
        HttpEntity<UserCredentials> entity = createCredentialsEntity(credentials);
        boolean success = false;
        try {
            restTemplate.exchange(baseUrl + "register", HttpMethod.POST, entity, Void.class);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<UserCredentials> createCredentialsEntity(UserCredentials credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(credentials, headers);
    }
    //for checking balance
    public void checkBalance(AuthenticatedUser credentials)  {
        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "viewBalance", HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            UserAccount userAccount = mapper.readValue(response.getBody(), UserAccount.class);
            System.out.printf("Your current account balance is: $%.2f%n", userAccount.getBalance());
        } catch (RestClientResponseException | ResourceAccessException |JsonProcessingException  e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
    }



    //for showing past transfers
    public void viewTransferHistory(AuthenticatedUser credentials)  {

        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();

            List<TransferMoney> allTransaction = mapper.readValue(response.getBody(), new TypeReference<List<TransferMoney>>() {});

            ConsoleService con = new ConsoleService();
            con.bal(allTransaction);

        } catch (RestClientResponseException | ResourceAccessException | JsonProcessingException  e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
    }




    //for showing past transfers
    public void viewPendingHistory(AuthenticatedUser credentials)  {

        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "pending", HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();

            List<TransferMoney> allTransaction = mapper.readValue(response.getBody(), new TypeReference<List<TransferMoney>>() {});

            ConsoleService con = new ConsoleService();
            con.bal(allTransaction);

        } catch (RestClientResponseException | ResourceAccessException | JsonProcessingException  e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
    }




    //show all the valid users
    public void getAllValidUsers(AuthenticatedUser credentials) {
        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "accounts", HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            List<UserAccount> userAccount = mapper.readValue(response.getBody(), new TypeReference<List<UserAccount>>() {});

            ConsoleService con = new ConsoleService();
            con.printValidUsers(userAccount);

        } catch (RestClientResponseException | ResourceAccessException | JsonProcessingException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
    }


    //send bucks
    public ServerMessage sendBucks(AuthenticatedUser credentials, int recipientId, int amount)  {
        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        // Create the form data
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("recipientId", recipientId);
        formData.add("amount", amount);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(formData, headers);
        ServerMessage serverMessaage = null;
        try {

            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "transfer", HttpMethod.POST, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            serverMessaage = mapper.readValue(response.getBody(), ServerMessage.class);

        }catch (RestClientResponseException  e) {
            ServerMessage sm = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                sm = mapper.readValue(e.getResponseBodyAsString(), ServerMessage.class);
            } catch (JsonProcessingException ex) {
                System.out.println(ex);
            }
            return sm;
        }catch ( ResourceAccessException | JsonProcessingException e) {
            System.out.println(e);
        }
        return serverMessaage;

    }
    //for viewCurrentBalance() in App
    public double getBalance(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        double balance = 0;

        try {
            ResponseEntity<Double> response = restTemplate.exchange(baseUrl + "balance", HttpMethod.GET, entity, Double.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return balance;
    }


    public void getTransaction(AuthenticatedUser credentials,List<UserAccount> userList) {
        String jwtToken = credentials.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void>  entity = new HttpEntity<>(headers);

        ConsoleService con = new ConsoleService();
        int transactionID = con.promptForInt("please enter the Id of the transaction for more info: ");

        try{
            ResponseEntity<String> transaction = restTemplate.exchange(baseUrl + "allTransfers",HttpMethod.GET,entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            List<TransferMoney> allTransaction = mapper.readValue(transaction.getBody(), new TypeReference<List<TransferMoney>>() {});
            boolean idFound = false;
            for (int x = 0;x < allTransaction.size() & !idFound;x++){
                if(transactionID == allTransaction.get(x).getTransferId()){
                    idFound = true;
                    con.oneTransaction(allTransaction.get(x),userList,credentials);
                }
            }
        } catch (RestClientResponseException | ResourceAccessException | JsonProcessingException e) {
        BasicLogger.log(e.getMessage());
        System.out.println(e);
        }
        catch (Exception e){
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
    }
    public List<UserAccount> getUsers(AuthenticatedUser credentials) {
        String jwtToken = credentials.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(baseUrl + "accounts", HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            List<UserAccount> userAccount = mapper.readValue(response.getBody(), new TypeReference<List<UserAccount>>() {});

            return userAccount;
        } catch (RestClientResponseException | ResourceAccessException | JsonProcessingException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e);
        }
        return null;
    }

}
