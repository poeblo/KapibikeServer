package com.kapiserver.service;

import com.fasterxml.uuid.Generators;
import com.kapiserver.config.DtSource;
import com.kapiserver.controller.ClientController;
import com.kapiserver.model.Client;
import com.kapiserver.model.Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class UserService implements Uservice {
    private static String pathToLogFolder = "/home/pavel/kapi_log/";
    private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar cal;
    private LocalDate localDate;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DataSource dtsource = DtSource.getDts();
    private final Logger LOG = LoggerFactory.getLogger(Uservice.class);

    @Override
    public int auth(String login, String password) {
        String url = "jdbc:postgresql://localhost/kapibike";
        String user = "server";
        String pass = "pavelsonykapibike";
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager
                    .getConnection(url,
                            user, pass);
            c.setAutoCommit(true);
            PreparedStatement stmt = c.prepareStatement("select * from masters where (login = ?) and (password = ?);");
            stmt.setString(1,login);
            stmt.setString(2,password);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int i;
                i = rs.getInt("id");
                if (i>0){
                    return i;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return 0;
    }


    @Override
    public List<Master> allMasters() {
        List<Master> users = new ArrayList<Master>();
        try{
            Connection c = dtsource.getConnection();
            c.setAutoCommit(true);
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select * from masters;");
            while (rs.next()){
                Master user = new Master();
                user.setLogin(rs.getString("login"));
                users.add(user);
            }
            st.close();
            rs.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return users;
    }

    @Override
    public Master masterById(int id) {
        LOG.info("MASTER ID"+id);
        try{
            Connection c = dtsource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement st = c.prepareStatement("select * from masters where id = ?");
            st.setInt(1,id);
            ResultSet rs = st.executeQuery();
            while (rs.next()){
                Master master = new Master();
                master.setId(rs.getInt("id"));
                master.setLogin(rs.getString("login"));
                master.setPermission(rs.getInt("permission"));
                return master;
            }
            st.close();
            rs.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Client clientByPhone(int phone){
        try {
            Connection c = dtsource.getConnection();
            PreparedStatement pr = c.prepareStatement("select * from clients where phone_number = ?");
            pr.setInt(1,phone);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                Client client = new Client();
                client.setId(rs.getString("id"));
                client.setPhone_number(rs.getInt("phone_number"));
                client.setName(rs.getString("name"));
                client.setMail(rs.getString("mail"));
                client.setBike_model(rs.getString("bike_model"));
                return client;
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
    public Client clientById(String id) {
        try {
            Connection c = dtsource.getConnection();
            PreparedStatement pr = c.prepareStatement("select * from clients where id = ?");
            pr.setString(1,id);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                Client client = new Client();
                client.setId(rs.getString("id"));
                client.setPhone_number(rs.getInt("phone_number"));
                client.setName(rs.getString("name"));
                client.setMail(rs.getString("mail"));
                client.setBike_model(rs.getString("bike_model"));
                return client;
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
    public void changeClientData(Client client) {
        Master master = masterById(ClientController.mid);
            if (master != null) {
                if (master.getPermission() == 3) {
                    localDate = LocalDate.now();
                    writeToFile(pathToLogFolder+"client_log"+dateFormat.format(localDate)+".txt", "MASTER "+master.getId()+
                            ":"+master.getLogin()+" CHANGE CLIENT DATA FOR CLIENT "+client.getId()+" СHANGES:\n"+
                            client.getName()+", "+client.getPhone_number()+", "+client.getMail()+", "+client.getBike_model());
                    try {
                        Connection c = dtsource.getConnection();
                        PreparedStatement pr = c.prepareStatement("update clients set phone_number = ?, name = ?, mail = ?, bike_model = ? where id = ?;");
                        pr.setInt(1, client.getPhone_number());
                        pr.setString(2, client.getName());
                        pr.setString(3, client.getMail());
                        pr.setString(4, client.getBike_model());
                        pr.setString(5, client.getId());
                        pr.execute();
                        pr.close();
                        c.close();
                    } catch (Exception e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }else{
                    writeToFile(pathToLogFolder+"client_log"+dateFormat.format(localDate)+".txt", "MASTER "+master.getId()+
                            ":"+master.getLogin()+" TRIED TO CHANGE CLIENT DATA FOR CLIENT "+client.getId());
                }
            }
    }

    @Override
    public String  addClient(Client client) {
        String id = "";
        try{
            Connection c = dtsource.getConnection();
            c.setAutoCommit(true);
            PreparedStatement pr;
            pr = c.prepareStatement("insert into clients (id, phone_number, name, mail, bike_model) values (?,?,?,?,?) returning id;");
            pr.setString(1, generateId());
            pr.setInt(2, client.getPhone_number());
            pr.setString(3, client.getName());
            pr.setString(4, client.getMail());
            pr.setString(5, client.getBike_model());
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                id = rs.getString("id");
            }
            rs.close();
            pr.close();
            c.close();
        }catch (Exception e){
            LOG.error(e.getLocalizedMessage());
        }
        return id;
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

    private String generateId(){
        UUID uuid = Generators.timeBasedGenerator().generate();
        LOG.info("TIME BASED"+uuid.toString());
        return uuid.toString();
    }
}
