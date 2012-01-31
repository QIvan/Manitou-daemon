package db;

import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
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
//          conn = new ConnectionDB("org.postgresql.Driver",
//                                  "jdbc:postgresql:test");
            conn = new ConnectionDB("org.sqlite.JDBC",
                                    "jdbc:sqlite:DB/MsgDB");
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
    public int parseMsg(Message msg) throws SQLException, MessagingException
    {
        String sender = "";
        String subject = "";
        String messageID = ""; //Mime ID
        String msgDate = "";
        String sentDate = Calendar.getInstance().getTime().toString();

        DataHandler dataHandler = msg.getDataHandler();
        Enumeration allHeaders = msg.getAllHeaders();
        while (allHeaders.hasMoreElements())
        {
            Header header = (Header) allHeaders.nextElement();
            if (header.getName().equals("Message-ID") )
                messageID = header.getValue();
            if (header.getName().equals("Received") )
            {
                msgDate = header.getValue();
                msgDate = msgDate.substring(msgDate.lastIndexOf(";") + 1);
            }

        }
        try { sender = msg.getFrom()[0].toString(); } catch (Exception e) {}
        try { subject = msg.getSubject(); } catch (Exception e) {}

        int mailID = -1;
        Statement st = bd.createStatement();
        st.execute("BEGIN");
        try {
            String queryInsert = "INSERT INTO "
                                 + "mail(sender, subject, msg_date, sender_date, status, message_id)"
                                 + " VALUES "
                                 + "('" + sender + "', '" + subject + "', '" + sentDate
                                 + "',  '" + msgDate + "', 0, '" + messageID + "')";

            System.out.println(queryInsert);
            st.execute(queryInsert);

            ResultSet rs = st.executeQuery("SELECT MAX(mail_id) FROM mail");
            rs.next();
            mailID = rs.getInt(1);

            String body = "";
            // TODO getContent() не всегда возвращает тело письма!
            if (dataHandler.getContentType().startsWith("text/plain"))
                body = dataHandler.getContent().toString();
            System.out.println("INSERT INTO body(mail_id, bodytext) VALUES "
                                + "(" + mailID + " , '" + body + "')");
            st.execute("INSERT INTO body(mail_id, bodytext) VALUES "
                        + "(" + mailID + " , '" + body + "')");


        }
        catch (SQLException e)
        {
            st.execute("ROLLBACK");
            throw e;
        }
        catch (IOException e)
        {

        }
        st.execute("COMMIT");
        return mailID;

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
