package db;

import net.ParseMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailMessage
{
    private static final int FROM = 1;
    private static final int TO = 2;
    private static final int CC = 3;
    private static final int REPLY_TO = 4;
    private static final int BCC = 5;
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
     */
    public int insertMessageInDB(Message msg)
            throws SQLException, MessagingException
    {

        int mailID = -1;
        Statement st = bd.createStatement();

        ParseMessage parser = new ParseMessage(msg);
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
        catch (IOException e)
        {
            st.execute("ROLLBACK");
            System.err.println("Parse Error in insertMessageInDB");
            e.printStackTrace();
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
                //InternetAddress addressFrom = new InternetAddress(headerRs.getString(1));
                //result.setFrom(addressFrom);
                result.setSubject(headerRs.getString(2));
                result.setSentDate(headerRs.getTimestamp(3));
            }

            addRecipientInMessage(result, id);

            ResultSet bodyRs = infoSt.executeQuery(
                    "Select bodytext from body where mail_id=" + id);
            if (bodyRs.next())
                result.setText(bodyRs.getString(1));

            result.saveChanges();
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

    private void addRecipientInMessage (Message msg, int id)
            throws SQLException
    {
        final Statement stAddresses = ConnectionDB.createStatement();
        ResultSet addressesRs = stAddresses.executeQuery(
                "Select addr_id, addr_type from mail_addresses"
                + " where mail_id=" + id);

        final Statement stEmails = ConnectionDB.createStatement();
        while (addressesRs.next())
        {
            final ResultSet emailsRs = stEmails.executeQuery(
                    "SELECT email_addr from addresses where addr_id="
                    + addressesRs.getInt(1));

            try
            {
                final InternetAddress email = new InternetAddress(emailsRs.getString(1));
                final int addr_type = addressesRs.getInt(2);
                switch (addr_type)
                {
                    case FROM:
                        msg.setFrom(email);
                        break;
                    case TO:
                        msg.addRecipient(Message.RecipientType.TO, email);
                        break;
                    case CC:
                        msg.addRecipient(Message.RecipientType.CC, email);
                        break;
                    case REPLY_TO:
                        InternetAddress replyToAddr[] = {email};
                        msg.setReplyTo(replyToAddr);
                        break;

                    case BCC:
                        msg.addRecipient(Message.RecipientType.BCC, email);
                        break;
                }
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
                System.err.println("Bad e-mail addresses!");
            }
        }
        stEmails.close();
        stAddresses.close();

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
