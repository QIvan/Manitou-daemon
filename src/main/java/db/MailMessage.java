package db;

import net.ParseMessage;

import javax.mail.Address;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* TODO refactor! Явно можно разбить на 2 класса:
   один собирает сообщение из базы,
   другой записывает новое в базу
*/
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
    // TODO refactor!
    public int insertMessageInDB(final Message msg)
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

            insertMailAdresses(msg, mailID);
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
    
    private void insertMailAdresses (final Message msg, final int id)
    {
        //я знаю что быдлокод, зато просто и понятно )
        try
        {
            final Address[] from = msg.getFrom();
            for (int i = 0; i < from.length; ++i)
                insertAddress(from[i], id, FROM, i);

            final Address[] recipientsTo = msg.getRecipients(Message.RecipientType.TO);
            for (int i = 0; i < recipientsTo.length; ++i)
            {
                insertAddress(recipientsTo[i], id, TO, i);
            }

            final Address[] recipientsCC = msg.getRecipients(Message.RecipientType.CC);
            for (int i = 0; i < recipientsCC.length; ++i)
            {
                insertAddress(recipientsCC[i], id, CC, i);
            }

            final Address[] recipientsBCC = msg.getRecipients(Message.RecipientType.BCC);
            for (int i = 0; i < recipientsBCC.length; ++i)
            {
                insertAddress(recipientsBCC[i], id, BCC, i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void insertAddress (final Address addr, final int mailId,
                                final int type, final int pos)
            throws SQLException
    {
        //проверить есть ли такой адрес в Базе
        final Statement st = ConnectionDB.createStatement();
        final ResultSet emailIdRs = st.executeQuery(
                "Select addr_id from addresses where email_addr='"
                + addr.toString() + "'");

        //если нет - добавить в таблицу addresses
        if (!emailIdRs.next())
        {
              //st.execute("Insert Into addresses Values ()");
        }
        //если есть - изменить nb_sent_to и last_sent_to или last_recv_from (в зависиомсти от типа)
        //взять addr_id и записать в mail_addresses с нужным типом

    }


    /**
     * @param id id сообщения в БД
     * @return возвращает сообщение в виде класса Message по переданному id
     */
    public Message createMessageOfDB(final int id)
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

    private void addRecipientInMessage (Message msg, final int id)
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
