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
    private static final String ADDRESS = "manitou.mail.test@gmail.com";
    private static final String TEXT_MAILS = "Test mail from testCase";
    private static final String SUBJECT = "Subject";
    private MailMessage mm;

    public MailMessageTest() throws Exception
    {
        mm = new MailMessage();
    }

    public void testParseMsg() throws Exception
    {
        Message message = this.createTestMessage();

        int id = mm.parseMsg(message);
        assertTrue(id != -1);
        System.out.println(id);

        Statement checkAdd = ConnectionDB.createStatement();

        ResultSet mailResult = checkAdd.executeQuery(
                "Select * from mail where mail_id=" + id);
        assertTrue(mailResult.next());
        assertFalse(mailResult.next());

        ResultSet status = checkAdd.executeQuery(
                "Select * from mail_status where mail_id = " + id);
        assertTrue(status.next());
        assertFalse(status.next());

        checkAdd.close();


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
        Statement st = ConnectionDB.createStatement();
        st.execute("DELETE FROM body");
        st.execute("DELETE FROM mail");
        st.execute("DELETE FROM mail_status");
        st.close();
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
