package db;

import junit.framework.TestCase;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Created 24.01.12 @ 14:48 by i.zemlyansky.
 */
public class MailMessageTest extends TestCase
{


    public static final String ADDRESS = "manitou.mail.test@gmail.com";
    public static final String TEXT_MAILS = "Test mail from testCase";

    public void testParseMsg() throws Exception
    {
        Message message = this.createTestMessage();
        MailMessage mm = new MailMessage();
        int id = mm.parseMsg(message);
        
        Connection db = mm.getConnect();
        ResultSet mailResult = db.createStatement().executeQuery("Select * from mail where mail_id=" + id);

        assertTrue(mailResult.next());


    }

    public void testGetMessagesToSend() throws Exception
    {

    }

    public void testCreateMessageOfDB() throws Exception
    {

    }



    private Message createTestMessage ()
    {
        Message message = null;
        try {
            //Создание письма
            message = new MimeMessage(Session.getDefaultInstance(new Properties()));
            message.setText(TEXT_MAILS);				//установка тела сообщения
            Address address = new InternetAddress(ADDRESS);
            message.setFrom(address);						 //добавление получателя

            Address toAddress = new InternetAddress(ADDRESS);
            Address ccAddress = new InternetAddress(ADDRESS);
            message.addRecipient(Message.RecipientType.TO, toAddress);
            message.addRecipient(Message.RecipientType.CC, ccAddress);
            message.saveChanges();
        }
        catch (Exception e)
        {
            fail();
        }
        return message;
    }
}
