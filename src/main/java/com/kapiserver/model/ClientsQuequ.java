package com.kapiserver.model;

import java.util.Calendar;

public class ClientsQuequ {
    Client client;
    Calendar date;

    public ClientsQuequ(){}

    public ClientsQuequ(Client client, Calendar calendar){
        this.client=client;
        this.date=calendar;
    }

    public void setClient(Client client){
        this.client=client;
    }

    public Calendar getDate() {
        return date;
    }

    public Client getClient() {
        return client;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
