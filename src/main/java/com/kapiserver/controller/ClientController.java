package com.kapiserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kapiserver.model.Client;
import com.kapiserver.service.UserService;
import com.kapiserver.service.Uservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class ClientController {
    public static int mid;
    private Uservice uservice = new UserService();
    private ObjectMapper om = new ObjectMapper();
    private final Logger LOG = LoggerFactory.getLogger(ClientController.class);


    @RequestMapping(value = "/auth",method = RequestMethod.GET)
    ResponseEntity<Void> authUser(){
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/client/{phone}", method = RequestMethod.GET)
    ResponseEntity<Client> clientByPhone (@PathVariable("phone")int phone){
        Client client = uservice.clientByPhone(phone);
        if (client==null){
            return new ResponseEntity<Client>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Client>(client,HttpStatus.OK);
    }

    @RequestMapping(value = "/client/change", method = RequestMethod.PUT)
    ResponseEntity<Void> changeClientData (@RequestBody Client client){
        try {
            LOG.info("TO EDIT:"+om.writeValueAsString(client));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (client!=null && client.getPhone_number()!=0) {
            uservice.changeClientData(client);
        }else{
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
