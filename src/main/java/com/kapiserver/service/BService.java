package com.kapiserver.service;

import com.kapiserver.model.Card;

public interface BService {
    Card getValue(int id);
    boolean setValue(Card card);
    Card addCard(Card card);
}
