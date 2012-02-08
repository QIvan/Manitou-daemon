package utils;

import db.MailMessage;
import net.gNetSettings;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created 02.02.12 @ 20:30 by i.zemlyansky.
 */
public class NewMail
{
    private static final String ADDRESS = "manitou.mail.test@gmail.com";
    private static final String SUBJECT = "Subject: New Mail";
    private static final String TEXT_MAILS = "Body text:\n Test mail \nfrom testCase";

    public static Message createTestMessage() throws Exception
    {
        //Создание письма
        Message message =
                new MimeMessage(Session.getDefaultInstance(new Properties()));
        message.setSubject(SUBJECT);
        message.setText(TEXT_MAILS);                //установка тела сообщения
        Address address = new InternetAddress(ADDRESS);
        message.setFrom(address);                         //добавление получателя

        Address toAddress = new InternetAddress(ADDRESS);
        Address ccAddress = new InternetAddress(ADDRESS);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.addRecipient(Message.RecipientType.CC, ccAddress);

        message.setSentDate(Calendar.getInstance().getTime());
        message.saveChanges();

        return message;
    }


    public static void sendTestMail() throws Exception
    {
        Message testMessage = createTestMessage();

        gNetSettings.getInstance().getSmtpTransport().sendMessage(testMessage,
                                                                  testMessage.getAllRecipients());
    }


    public static void putInDbTestMail() throws Exception
    {
        Message testMessage = createTestMessage();

        MailMessage mm = new MailMessage();
        mm.insertMessageInDB(testMessage);
    }
}
