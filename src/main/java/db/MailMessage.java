package db;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailMessage
{

    private static ConnectionDB conn;
    private static Connection bd;

    public MailMessage()
    {
        try
        {
            conn = new ConnectionDB("org.postgresql.Driver",
                                    "jdbc:postgresql:test?user=ivan&password=qwertyui");
            bd = conn.getConnect();
        }
        catch (Exception ex)
        {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnect() throws Exception
    {
        return conn.getConnect();
    }

    /**
     * Записывает в БД сообщение msg
     *
     * @param msg looks like a message
     * @return возвращает id сообщения из таблицы mail
     * @throws Exception looks like SQLException
     */
    public int parseMsg(Message msg) throws Exception
    {
        Statement st = bd.createStatement();

        String sender = "";
        String subject = "";
        String sentDate = new Date().toString();
        int messageNumber = 0;

        try { sender = msg.getFrom()[0].toString(); } catch (Exception e) {}
        try { subject = msg.getSubject(); } catch (Exception e) {}
        try { sentDate = msg.getSentDate().toString(); } catch (Exception e) {}
        try { messageNumber = msg.getMessageNumber(); } catch (Exception e) {}

        String query = "INSERT INTO "
                       + "mail(sender, subject, msg_date, sender_date, status, message_id)"
                       + " VALUES "
                       + "('" + sender + "', '" + subject + "', '" + sentDate
                       + "',  now(), 1, '" + messageNumber + "')";

        System.out.println(query);
        st.execute(query);

        ResultSet rs = st.executeQuery("Select mail_id from mail ");
        rs.next();
        return rs.getInt(1);

    }


    /**
     * @param id id сообщения в БД
     * @return возвращает сообщение в виде класса Message по переданному id
     */
    public Message createMessageOfDB(int id)
    {
        Message result = new MimeMessage(Session.getDefaultInstance(null));
        try
        {
            Statement infoSt = bd.createStatement();
            ResultSet infoRs = infoSt.executeQuery(
                    "Select sender, subject, msg_date from mail where mail_id="
                    + id);
            if (infoRs.next())
            {
                result.setFrom(new InternetAddress(infoRs.getString(1)));
                result.setSubject(infoRs.getString(2));
                result.setSentDate(infoRs.getDate(3));
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE,
                                                              null,
                                                              ex);
        }
        return result;
    }


    public ArrayList<Integer> getMessagesToSend()
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        try
        {
            ResultSet mailIDs = bd.prepareStatement(
                    "Select mail_id from mail_status").executeQuery();
            while (mailIDs.next())
                result.add(mailIDs.getInt(1));
        }
        catch (SQLException ex)
        {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE,
                                                              null,
                                                              ex);
        }
        return result;
    }

}
