package com.kapiserver.service;

import com.kapiserver.model.Client;
import com.kapiserver.model.Master;

import java.util.List;

public interface Uservice {
    String addClient (Client client);
    int auth (String login, String password);
    List<Master> allMasters();
    Master masterById(int id);
    Client clientByPhone(int phone);
    Client clientById(String id);
    void changeClientData(Client client);
}
