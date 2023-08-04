package com.techelevator.tenmo.services;

public class ServerMessage {

    private String type;
    private String message;

    public ServerMessage() {
        // TODO Auto-generated constructor stub
    }


    public ServerMessage(String type, String message) {
        super();
        this.type = type;
        this.message = message;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }




}

