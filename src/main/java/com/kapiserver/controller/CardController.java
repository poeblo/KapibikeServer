package com.kapiserver.controller;

import com.kapiserver.model.Card;
import com.kapiserver.service.BService;
import com.kapiserver.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank")
public class CardController {
    private BService bankservice = new BankService();
    private final Logger LOG = LoggerFactory.getLogger(CardController.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Card> getById(@PathVariable("id")int id){
        Card card = bankservice.getValue(id);
        if (card.getId()==0){
            return new ResponseEntity<Card>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Card>(card,HttpStatus.OK);
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Card> addCard(@RequestBody Card card){
        Card card1 = bankservice.addCard(card);
        if (card1.getId()==0){
            return new ResponseEntity<Card>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Card>(card1, HttpStatus.CREATED);
    }
    @RequestMapping(value = "/value", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> setValue(@RequestBody Card card){
        boolean flag = bankservice.setValue(card);
        if (!flag){
            return new ResponseEntity<Boolean>(flag,HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Boolean>(flag,HttpStatus.OK);
    }
}
