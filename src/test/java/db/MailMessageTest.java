package db;

import junit.framework.TestCase;
import utils.DeleteMails;
import utils.NewMail;

import javax.mail.Message;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created 24.01.12 @ 14:48 by i.zemlyansky.
 */
public class MailMessageTest extends TestCase
{

    private final MailMessage mm;

    public MailMessageTest() throws Exception
    {
        mm = new MailMessage();
    }

    public void testParseMsg() throws Exception
    {
        Message message = NewMail.createTestMessage();

        int id = mm.insertMessageInDB(message);
        assertTrue(id != -1);
        System.out.println(id);

        Statement checkAdd = ConnectionDB.createStatement();

        //TODO сделать проверку на значения
        ResultSet mail = checkAdd.executeQuery(
                "Select * from mail where mail_id=" + id);
        assertTrue(mail.next());
        assertFalse(mail.next());

        ResultSet status = checkAdd.executeQuery(
                "Select status from mail_status where mail_id = " + id);
        assertTrue(status.next());
        assertFalse(status.next());

        ResultSet body = checkAdd.executeQuery(
                "Select * from body where mail_id = " + id);
        assertTrue(body.next());
        assertFalse(body.next());

        ResultSet header = checkAdd.executeQuery(
                "Select * from header where mail_id = " + id);
        assertTrue(header.next());
        assertFalse(header.next());

        checkAdd.close();


    }

    public void testGetMessagesToSend() throws Exception
    {

    }

    public void testCreateMessageOfDB() throws Exception
    {
        Message testMessage = NewMail.createTestMessage();
        int id = mm.insertMessageInDB(testMessage);

        Message messageOfDB = mm.createMessageOfDB(id);

        // check Subject
        System.out.println("Subject of DB: " + messageOfDB.getSubject() +
                           "\nSubject test: " + testMessage.getSubject());
        assertTrue(messageOfDB.getSubject().equals(testMessage.getSubject()));
        // check From Address
        System.out.println("From of DB: " + messageOfDB.getFrom() +
                           "\nFrom test: " + testMessage.getFrom());
        assertTrue(Arrays.equals(messageOfDB.getFrom(), testMessage.getFrom()));
        // check Date
        System.out.println("Date of DB: " + messageOfDB.getSentDate() +
                           "\nDate test: " + testMessage.getSentDate());
        assertTrue(messageOfDB.getSentDate().equals(testMessage.getSentDate()));
        // TODO сделать проверку на тело сообщения.

//        System.out.println("\n" + messageOfDB.getSentDate().toString());
//        System.out.println(testMessage.getSentDate().toString());

    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        DeleteMails.DeleteAllInDB();
    }



}
