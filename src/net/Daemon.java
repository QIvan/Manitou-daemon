/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPNestedMessage;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMultipart;
import db.MailMessage;

import com.sun.mail.imap.protocol.FLAGS;
import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.smtp.SMTPSSLTransport;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ivan
 */
//test
public class Daemon
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {


        try {
            MailMessage mm = new MailMessage();
            ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
            System.out.print(messagesToSend);
            //msg.writeTo(System.out);
            /*System.out.print("\n\n");
            Store store = gNetSettings.getInstance().getPopConnect();
            System.out.println("Connect status is " + store.isConnected());
            //session.setDebug(true);

            //Создание письма
            Message message = new MimeMessage(session);
            message.setText("Test mail from NS90");				//установка тела сообщения
            Address address = new InternetAddress("manitou.mail.test@gmail.com");
            message.setFrom(address);						 //добавление получателя	
            
            Address toAddress = new InternetAddress("manitou.mail.test@gmail.com");
            Address ccAddress = new InternetAddress("manitou.mail.test@gmail.com");
            message.addRecipient(Message.RecipientType.TO, toAddress);
            message.addRecipient(Message.RecipientType.CC, ccAddress);
            message.saveChanges(); // implicit with send()

            gNetSettings.getInstance().getSmtpTransport().sendMessage(message, message.getAllRecipients());

            // Open the folder
            Folder inbox = store.getFolder("INBOX");
            if (inbox == null) {
            System.out.println("No INBOX");
            System.exit(1);
            }
            inbox.open(Folder.READ_WRITE);
            // Get the messages from the server
            Message[] messages = inbox.getMessages();
            System.out.println("Messages count = " + messages.length);
            //MailMessage.parseMsg(messages[0]);
            if (messages.length != 0)
            {
            //for (int i = 0; i < messages.length; i++) {
            System.out.println("------------ Message " + messages[0].getMessageNumber()
            + " ------------");
            System.out.println(messages[0].getContent());
            messages[0].writeTo(System.out);
            //System.out.print(((MimeMultipart)messages[0].getContent()).getBodyPart(0).getContent().toString());
            System.out.print("\n\n");
            messages[0].setFlag(FLAGS.Flag.DELETED, true);
            //}
            }
            else
            {
            System.out.println("MailBox is empty.");
            }
            // Close the connection
            // but don't remove the messages from the server
            inbox.close(true);
            store.close();/**/

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private void sendAllPost() throws MessagingException
    {
        MailMessage mm = new MailMessage();
        ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
        Transport transport = gNetSettings.getInstance().getSmtpTransport();
        for (Integer id : messagesToSend) {
            Message message = mm.createMessageOfDB(id);
            transport.sendMessage(message, message.getAllRecipients());
        }
    }
}

