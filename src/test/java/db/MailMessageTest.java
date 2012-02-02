package db;

import junit.framework.TestCase;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created 24.01.12 @ 14:48 by i.zemlyansky.
 */
public class MailMessageTest extends TestCase
{


    public static final String ADDRESS = "manitou.mail.test@gmail.com";
    public static final String TEXT_MAILS = "Test mail from testCase";
    public static final String SUBJECT = "Subject";
    private MailMessage mm;
    private Statement st;


    public MailMessageTest() throws Exception
    {
        mm = new MailMessage();
        st = ConnectionDB.createStatement();
    }

    public void testParseMsg() throws Exception
    {
        Message message = this.createTestMessage();

        int id = mm.parseMsg(message);
        assertTrue(id != -1);
        System.out.println(id);

        ResultSet mailResult = st.executeQuery(
                "Select * from mail where mail_id=" + id);

        assertTrue(mailResult.next());
    }

    public void testGetMessagesToSend() throws Exception
    {

    }

    public void testCreateMessageOfDB() throws Exception
    {
        Message testMessage = this.createTestMessage();
        int id = mm.parseMsg(testMessage);

        Message messageOfDB = mm.createMessageOfDB(id);

        assertTrue(messageOfDB.getSubject().equals(testMessage.getSubject()));
        assertTrue(Arrays.equals(messageOfDB.getFrom(), testMessage.getFrom()));
        System.out.println("\n" + messageOfDB.getSentDate().toString());
        System.out.println(testMessage.getSentDate().toString());
        assertTrue(messageOfDB.getSentDate().equals(testMessage.getSentDate()));
        // TODO сделать проверку на тело сообщения.
        //assertTrue(messageOfDB.getReceivedDate().equals(testMessage.getReceivedDate()));
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        st.execute("DELETE FROM body");
        st.execute("DELETE FROM mail");
    }


    private Message createTestMessage ()
    {
        Message message = null;
        try {
            //Создание письма
            message = new MimeMessage(Session.getDefaultInstance(new Properties()));
            message.setSubject(SUBJECT);
            message.setText(TEXT_MAILS);				//установка тела сообщения
            Address address = new InternetAddress(ADDRESS);
            message.setFrom(address);						 //добавление получателя

            Address toAddress = new InternetAddress(ADDRESS);
            Address ccAddress = new InternetAddress(ADDRESS);
            message.addRecipient(Message.RecipientType.TO, toAddress);
            message.addRecipient(Message.RecipientType.CC, ccAddress);

            message.setSentDate(Calendar.getInstance().getTime());
            message.saveChanges();
        }
        catch (Exception e)
        {
            fail();
        }
        return message;
    }
}
