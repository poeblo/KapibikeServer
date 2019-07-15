package com.kapiserver.model;

public class Master {
    private int id;
    private String login;
    private String password;
    private int permission;

    public Master(){}

    public Master(int id, String  login, String password, int permission){
        this.id=id;
        this.login=login;
        this.password=password;
        this.permission = permission;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
