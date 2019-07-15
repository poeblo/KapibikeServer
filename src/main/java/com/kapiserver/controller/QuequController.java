package com.kapiserver.controller;


import com.kapiserver.model.ClientsQuequ;
import com.kapiserver.service.QService;
import com.kapiserver.service.QuequService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quequ")
public class QuequController {
    private final Logger LOG = LoggerFactory.getLogger(QuequController.class);
    private QService qservice = new QuequService();


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<ClientsQuequ>> getAll(){
        List<ClientsQuequ> quequ=qservice.getAll();
        if (quequ.isEmpty()){
            return new ResponseEntity<List<ClientsQuequ>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<ClientsQuequ>>(quequ,HttpStatus.OK);
    }

}

