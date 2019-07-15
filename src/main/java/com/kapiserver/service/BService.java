package com.kapiserver.service;

import com.kapiserver.model.Card;

import java.util.List;

public interface BService {
    Card cardById(int id);
    Card cardByPhone(int phone);
    void updateBalance(Card card);
    boolean addCard(Card card);
    List<Card> allCards();
}
