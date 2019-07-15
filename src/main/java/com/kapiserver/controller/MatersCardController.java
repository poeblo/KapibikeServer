package com.kapiserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kapiserver.model.Card;
import com.kapiserver.service.BService;
import com.kapiserver.service.CardService;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
public class MatersCardController {
    private BService bankservice = new CardService();
    private ObjectMapper om = new ObjectMapper();

    private final Logger LOG = LoggerFactory.getLogger(MatersCardController.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Card> getById(@PathVariable("id")int id){
        Card card = bankservice.cardById(id);
        if (card==null) {
            return new ResponseEntity<Card>(HttpStatus.NO_CONTENT);
        }
        if (card.getId() == 0 || card.getOwner() == null) {
            return new ResponseEntity<Card>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Card>(card,HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<Card>> allCards(){
        List<Card> cards = bankservice.allCards();
        if (cards!=null) {
            return new ResponseEntity<List<Card>>(bankservice.allCards(), HttpStatus.OK);
        }
        return new ResponseEntity<List<Card>>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/phone/{phone}", method = RequestMethod.GET)
    public ResponseEntity<Card> cardByPhone (@PathVariable("phone") int phone){
        Card card = bankservice.cardByPhone(phone);
        if (card!=null){
            if (card.getId()>0){
                return new ResponseEntity<Card>(card,HttpStatus.OK);
            }
        }
        return new ResponseEntity<Card>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Void> addCard(@RequestBody Card card){
        try {
            LOG.info("ADD NEW CARD---------"+om.writeValueAsString(card));
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        if (!bankservice.addCard(card)){
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/balance/change", method = RequestMethod.PUT)
    public ResponseEntity<Void> setBalance(@RequestBody Card card){
        bankservice.updateBalance(card);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
