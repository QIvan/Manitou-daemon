/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import db.MailMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author ivan
 */
//test
public class Daemon
{
    public static final String MAIL_TEXT = "New Mail";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {


        try {
            Session session = Session.getDefaultInstance(new Properties());
            //Создание письма
            Message message = new MimeMessage(session);
            message.setSubject(MAIL_TEXT);
            message.setText(MAIL_TEXT);				//установка тела сообщения
            Address address = new InternetAddress("manitou@qivan");
            message.setFrom(address);						 //добавление получателя

            Address toAddress = new InternetAddress("manitou.mail.test@gmail.com");
            message.addRecipient(Message.RecipientType.TO, toAddress);
            message.saveChanges(); // implicit with send()

            gNetSettings.getInstance().getSmtpTransport().sendMessage(message, message.getAllRecipients());
            /**/

            Connection db = new MailMessage().getConnect();
            Store pop = gNetSettings.getInstance().getPopConnect();
            Store imap = gNetSettings.getInstance().getImapConnect();

            Folder folder = imap.getFolder("INBOX");
            /*folder.open(Folder.READ_WRITE);             //Здесь помечаются сообщения как прочитанные
            boolean newMessages = folder.hasNewMessages();

            System.out.println("\nIs new message: " + newMessages);
            System.out.println("Count new message: " + folder.getNewMessageCount());
            //folder.setFlags(1, 2, new Flags (Flags.Flag.DELETED), true);
            System.out.println("Count message in folder:" + folder.getMessageCount() + "\n");*/

            Message[]  messages = getNewMail("imap");
            printMessages(messages);

            System.out.println("\n\nCount new message: " + messages.length);


//            MailMessage mm = new MailMessage();
            /*ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
            System.out.print(messagesToSend);*/
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

    private static Message[] getNewMail(String type) throws MessagingException
    {
        Message[] result = new Message[0];
        Store protocol;
        if (type == "imap")
            protocol = gNetSettings.getInstance().getImapConnect();
        else
            protocol = gNetSettings.getInstance().getPopConnect();

        Folder folder = protocol.getFolder("INBOX");

        System.out.println("Is new mess " + folder.hasNewMessages());
        System.out.println("Count new message: " + folder.getNewMessageCount());
        //folder.setFlags(1, 2, new Flags (Flags.Flag.DELETED), true);
        System.out.println("Count message in folder:" + folder.getMessageCount() + "\n");

        folder.open(Folder.READ_ONLY);
        if (folder.hasNewMessages())
        {
            result = folder.getMessages(folder.getMessageCount() - folder.getNewMessageCount(), folder.getMessageCount());
        }
        folder.close(true);

        return result;
    }


    private static void printMessages(Message[] messages) throws
                                                          IOException, MessagingException
    {
        for (Message msg : messages)
        {
            System.out.println(msg.getSubject());
            System.out.println(msg.getContent().toString());


            System.out.println("\n\n");
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

