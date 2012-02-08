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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailMessage
{
    private  Connection bd;

    public MailMessage()
    {
        try
        {
            bd = ConnectionDB.getInstance().getConnect();
        }
        catch (Exception ex)
        {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Записывает в БД сообщение msg
     *
     * @param msg looks like a message
     * @return id Message from table "mail"
     * @throws Exception looks like SQLException
     */
    public int insertMessageInDB(Message msg)
            throws SQLException, MessagingException, ParseException
    {
        String sender = "";
        String subject = "";
        String messageID = ""; //Mime ID
        String senderDate = "";
        String acceptDate = String.valueOf(Calendar.getInstance().getTime().getTime());

        DataHandler dataHandler = msg.getDataHandler();
        Enumeration allHeaders = msg.getAllHeaders();
        while (allHeaders.hasMoreElements())
        {
            Header header = (Header) allHeaders.nextElement();
            if (header.getName().equals("Message-ID"))
                messageID = header.getValue();
            if (header.getName().equals("Received"))
            {
                senderDate = header.getValue();
                senderDate = senderDate.substring(senderDate.lastIndexOf(";") + 1);



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
                                 + "('"
                                 + sender
                                 + "', '"
                                 + subject
                                 + "', '"
                                 + acceptDate
                                 + "',  '"
                                 + senderDate
                                 + "', 0, '"
                                 + messageID
                                 + "')";

            System.out.println(queryInsert);
            st.execute(queryInsert);

            ResultSet rs = st.executeQuery("SELECT MAX(mail_id) FROM mail");
            rs.next();
            mailID = rs.getInt(1);

            // TODO getContent() не всегда возвращает тело письма!
            String body = "";
            if (dataHandler.getContentType().startsWith("text/plain"))
                body = dataHandler.getContent().toString();

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
        st.close();
        return mailID;

    }


    /**
     * @param id id сообщения в БД
     * @return возвращает сообщение в виде класса Message по переданному id
     */
    public Message createMessageOfDB(int id)
    {
        Message result = new MimeMessage(Session.getDefaultInstance(new Properties()));
        try
        {
            Statement infoSt = bd.createStatement();
            ResultSet headerRs = infoSt.executeQuery(
                    "Select sender, subject, msg_date from mail where mail_id="
                    + id);
            if (headerRs.next())
            {
                InternetAddress addressTo = new InternetAddress(headerRs.getString(1));
                result.setFrom(addressTo);
                result.addRecipient(Message.RecipientType.TO, addressTo);
                result.setSubject(headerRs.getString(2));
                result.setSentDate(headerRs.getTimestamp(3));
            }

            ResultSet bodyRs = infoSt.executeQuery(
                               "Select bodytext from body where mail_id=" + id);

            if (bodyRs.next())
                result.setText(bodyRs.getString(1));

            infoSt.close();

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
            Statement selectMailForSend = ConnectionDB.createStatement();
            ResultSet mailIDs = selectMailForSend.executeQuery(
                    "Select mail_id from mail where status & 128 = 128");
            while (mailIDs.next())
                result.add(mailIDs.getInt(1));
            selectMailForSend.close();
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
