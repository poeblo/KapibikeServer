package com.kapiserver.model;

public class Card {
    private int id;
    private int balance;
    private Client owner;


    public Card(){}

    public Card(int id, int balance, Client owner){
        this.id=id;
        this.balance = balance;
        this.owner=owner;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
