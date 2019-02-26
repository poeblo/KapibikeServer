package com.kapiserver.model;

public class Card {
    private int id;
    private int value;
    private int phone;
    private String mail;
    private String name;

    public Card(){}

    public Card(int id, int value, int phone, String mail, String name){
        this.id=id;
        this.value=value;
        this.phone=phone;
        this.mail=mail;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
