package com.kapiserver.service;

import com.kapiserver.config.DtSource;
import com.kapiserver.model.Client;
import com.kapiserver.model.ClientsQuequ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Calendar;
import java.util.List;

public class QuequService implements QService {
    private DataSource dtsource = DtSource.getDts();
    private final Logger LOG = LoggerFactory.getLogger(QService.class);

    @Override
    public void addToQuequ(Client client, Calendar cal) {

    }

    @Override
    public List<ClientsQuequ> getAll() {
        return null;
    }
}
