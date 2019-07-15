package com.kapiserver.service;

import com.kapiserver.model.Client;
import com.kapiserver.model.ClientsQuequ;

import java.util.Calendar;
import java.util.List;

public interface QService {
    void addToQuequ(Client client, Calendar cal);
    List<ClientsQuequ> getAll();
}
