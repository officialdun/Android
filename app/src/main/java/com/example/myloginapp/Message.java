package com.example.myloginapp;

public class Message {

    private String sender;
    private String Receiver;
    private String content;

     public Message(){

     }

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        Receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




}
