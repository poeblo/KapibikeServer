package com.kapiserver.service;

import com.kapiserver.config.DtSource;
import com.kapiserver.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BankService implements BService{
    private DataSource dataSource = DtSource.getDts();
    private final Logger LOG = LoggerFactory.getLogger(BankService.class);

    @Override
    public Card getValue(int id) {
        Card card = new Card();
        try{
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("select * from card;");
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                card.setId(rs.getInt("id"));
                card.setValue(rs.getInt("value"));
                card.setPhone(rs.getInt("phone"));
                card.setMail(rs.getString("mail"));
                card.setName(rs.getString("mail"));
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return card;
    }

    @Override
    public boolean setValue(Card card) {
        boolean flag = false;
        try{
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("update card set value = ? where id = ? returning value;");
            pr.setInt(1,card.getValue());
            pr.setInt(2,card.getId());
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                if (rs.getInt("value")==card.getValue()){
                    flag=true;
                }
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return flag;
    }

    @Override
    public Card addCard(Card card) {
        Card retcard = new Card();
        try{
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("insert into card values (?,?,?,?,?) returning *;");
            pr.setInt(1,card.getId());
            pr.setInt(2,card.getValue());
            pr.setInt(3,card.getPhone());
            pr.setString(4,card.getMail());
            pr.setString(5,card.getName());
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                retcard.setId(rs.getInt("id"));
                retcard.setValue(rs.getInt("value"));
                retcard.setPhone(rs.getInt("phone"));
                retcard.setMail(rs.getString("mail"));
                retcard.setName(rs.getString("name"));
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return retcard;
    }
}
