package com.kapiserver.service;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.fasterxml.uuid.Generators;
import com.kapiserver.config.DtSource;
import com.kapiserver.controller.ClientController;
import com.kapiserver.model.Card;
import com.kapiserver.model.Client;
import com.kapiserver.model.Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class CardService implements BService{
    private static String pathToLogFolder = "/home/pavel/kapi_log/";
    private DataSource dataSource = DtSource.getDts();
    private final Logger LOG = LoggerFactory.getLogger(CardService.class);
    private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate localDate;
    private Calendar cal;
    private Uservice uservice =  new UserService();

    @Override
    public List<Card> allCards() {
        List<Card> cards  = new ArrayList<>();
        try {
            Connection c = dataSource.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select * from cards inner join clients on cards.owner = clients.id;");
            while (rs.next()){
                Card card = new Card();
                Client client =  new Client();
                card.setId(rs.getInt("id"));
                card.setBalance(rs.getInt("balance"));
                client.setId(rs.getString("owner"));
                client.setPhone_number(rs.getInt("phone_number"));
                client.setName(rs.getString("name"));
                client.setBike_model(rs.getString("bike_model"));
                client.setMail(rs.getString("mail"));
                card.setOwner(client);
                cards.add(card);
            }
            st.close();
            rs.close();
            c.close();
            return cards;

        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Card cardById(int id) {
        Card card = new Card();
        Client client = new Client();
        try{
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("select * from cards inner join clients on cards.owner = clients.id where cards.id = ?;");
            pr.setInt(1,id);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                card.setId(rs.getInt("id"));
                card.setBalance(rs.getInt("balance"));
                client.setId(rs.getString("owner"));
                client.setPhone_number(rs.getInt("phone_number"));
                client.setMail(rs.getString("mail"));
                client.setName(rs.getString("name"));
                client.setBike_model(rs.getString("bike_model"));
                card.setOwner(client);
                return card;
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Card cardByPhone(int phone) {
        Card card= new Card();
        Client client = new Client();
        try {
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("select * from cards inner join clients on cards.owner = clients.id where clients.phone_number = ?;");
            pr.setInt(1,phone);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                card.setId(rs.getInt("id"));
                card.setBalance(rs.getInt("balance"));
                client.setId(rs.getString("owner"));
                client.setPhone_number(rs.getInt("phone_number"));
                client.setName(rs.getString("name"));
                client.setBike_model(rs.getString("bike_model"));
                client.setMail(rs.getString("mail"));
                card.setOwner(client);
            }
            rs.close();
            pr.close();
            c.close();
            return card;
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void updateBalance(Card card) {
        LOG.info("UPDATE BALANCE FOR CARD"+card.getId());
        Master master = uservice.masterById(ClientController.mid);
        if (master!=null) {
            if (master.getPermission()==3) {
                localDate = LocalDate.now();
                try {
                    writeToFile(pathToLogFolder + "/balance_log-" + dateFormat.format(localDate) + ".txt", "MASTER (id,login)" + master.getId() + ":" + master.getLogin().replace(" ", "") + " CHANGE BALANCE CARD ID:" + card.getId() + " FROM:" + cardBalance(card.getId()) + " TO:" + card.getBalance());
                    Connection c = dataSource.getConnection();
                    c.setAutoCommit(true);
                    PreparedStatement pr = c.prepareStatement("update cards set balance = ? where id = ? returning balance;");
                    pr.setInt(1, card.getBalance());
                    pr.setInt(2, card.getId());
                    pr.execute();
                    pr.close();
                    c.close();
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }else{
                localDate = LocalDate.now();
                try {
                    writeToFile(pathToLogFolder + "/balance_log-" + dateFormat.format(localDate) + ".txt",
                            "MASTER (id,login): " + master.getId() + ":" + master.getLogin().replace(" ", "") + " HAD TRIED TO CHANGE BALANCE TO:" + card.getBalance()+" FOR CARD ID:" + card.getId());
                }catch (Exception e){
                    LOG.error(e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public boolean addCard(Card card) {
        Master master = uservice.masterById(ClientController.mid);
        if (cardById(card.getId())==null) {
            if (master != null) {
                if (master.getPermission() == 3) {
                    localDate = LocalDate.now();
                    try {
                        String id = "";
                        Client client = uservice.clientByPhone(card.getOwner().getPhone_number());
                        if (client != null && client.getId().length() > 0) {
                            return false;
                        } else{
                            id = uservice.addClient(card.getOwner());
                        }
                        if (id.length()>0) {
                            Connection c = dataSource.getConnection();
                            c.setAutoCommit(true);
                            //LOG.info("OWNER_PHONE----"+card.getOwner().getPhone_number());
                            PreparedStatement pr1 = c.prepareStatement("insert into cards (id, balance, owner) values (?,?,?) returning id;");
                            pr1.setInt(1, card.getId());
                            pr1.setInt(2, card.getBalance());
                            pr1.setString(3, id);
                            ResultSet rs = pr1.executeQuery();
                            while (rs.next()) {
                                if (rs.getInt("id") == card.getId()) {
                                    writeToFile(pathToLogFolder + "card_log" + dateFormat.format(localDate) + ".txt", "MASTER:" + master.getLogin().replace(" ", "") + "ADD NEW CARD ID: " + card.getId() + " WITH OWNER:" + card.getOwner().getPhone_number());
                                    return true;
                                }
                            }
                            rs.close();
                            pr1.close();
                            c.close();
                        }
                    } catch (Exception e) {
                        LOG.error(e.getLocalizedMessage());
                        LOG.error(e.getStackTrace().toString());
                    }
                } else {
                    localDate = LocalDate.now();
                    try {
                        writeToFile(pathToLogFolder + "card_log" + dateFormat.format(localDate) + ".txt", "MASTER:" + master.getLogin().replace(" ", "") + "HAD TRIED ADD NEW CARD ID: " + card.getId() + " WITH OWNER:" + card.getOwner().getPhone_number());
                    } catch (Exception e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }
            }
        }
        return false;
    }

    private int cardBalance(int id){
        try{
            Connection c = dataSource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr = c.prepareStatement("select balance from cards where id = ?");
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                return  rs.getInt("balance");
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return -1;
    }


    private void writeToFile(String fileName, String text)  {
        File folder = new File(pathToLogFolder);
        if (!folder.exists()){
            folder.mkdir();
        }
        File file = new File(fileName);
        try {
            if (file.createNewFile()) {
                LOG.info(fileName + " File Created");
            } else {
                LOG.info("File " + fileName + " already exists");
            }
            cal = Calendar.getInstance();
            String time = dateTimeFormat.format(cal.getTime());
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(time + "----" + text + "\n");
            writer.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
    }
}
