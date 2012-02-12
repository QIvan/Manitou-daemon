package db;

import net.ParseMessage;

import javax.activation.DataHandler;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
        ParseMessage parser = new ParseMessage(msg);


        String senderDate = "";


        DataHandler dataHandler = msg.getDataHandler();
        Enumeration allHeaders = msg.getAllHeaders();
        while (allHeaders.hasMoreElements())
        {
            Header header = (Header) allHeaders.nextElement();
            if (header.getName().equals("Received"))
            {
                senderDate = header.getValue();
                senderDate = senderDate.substring(senderDate.lastIndexOf(";") + 1);
            }

        }



        int mailID = -1;
        Statement st = bd.createStatement();

        st.execute("BEGIN");
        try {
            String queryMail = "INSERT INTO "
                                 + "mail(sender, subject, msg_date, sender_date, status, message_id)"
                                 + " VALUES "
                                 + "('"
                                 + parser.getFrom()
                                 + "', '"
                                 + parser.getSubject()
                                 + "', '"
                                 + String.valueOf(Calendar.getInstance().getTime().getTime())
                                 + "',  '"
                                 + parser.getSentDate().getTime()
                                 + "', 0, '"
                                 + parser.getMessageID()
                                 + "')";

            System.out.println(queryMail);
            st.execute(queryMail);

            ResultSet rs = st.executeQuery("SELECT MAX(mail_id) FROM mail");
            rs.next();
            mailID = rs.getInt(1);

            String queryBody = "INSERT INTO body(mail_id, bodytext) VALUES "
                               + "(" + mailID + " , '" + parser.getBody() + "')";
            System.out.println(queryBody);
            st.execute(queryBody);

            String queryHeader = "INSERT INTO header VALUES "
                       + "(" + mailID + " , '" + parser.getHeader() + "')";
            System.out.println(queryHeader);
            st.execute(queryHeader);

        }
        catch (Exception e)
        {
            st.execute("ROLLBACK");
            e.printStackTrace();
            throw new ParseException("Insert Error", 0);
        }
        st.execute("COMMIT");
        st.close();
        return mailID;

    }




    private String getTextMail(Message msg)
    {
        InputStream inputStream ;
        try
        {
            inputStream = msg.getDataHandler().getInputStream();
        }
        catch (Exception e)
        {
            return null;
        }

        int bytes_read = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte data[] = new byte[1024];

        try {
        while((bytes_read = inputStream.read(data)) >0)
            baos.write(data, 0, bytes_read);
        inputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return baos.toString();
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
