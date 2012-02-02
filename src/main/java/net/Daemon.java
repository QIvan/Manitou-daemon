/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import db.ConnectionDB;
import db.MailMessage;

import javax.activation.CommandInfo;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;


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
            sendAllMail();


/**/                                      /*
            MailMessage mm1 = new MailMessage();
            Statement statement = mm1.getConnect().createStatement();
            statement.executeUpdate(
                            "Insert into mail_status values (10, 10)"
                            );
            MailMessage mm = new MailMessage();
            Store pop = gNetSettings.getInstance().getPopConnect();
            Store imap = gNetSettings.getInstance().getImapConnect();


            Statement st = mm.getConnect().createStatement();
                        st.executeUpdate(
                                        "Insert into mail_status values (10, 10)"
                                        );
           /*             st.executeUpdate("UPDATE mail SET status=status | 128");
            System.out.print("UPDATE mail SET status=status & ~128 where mail_id = 4");

            Folder folder = imap.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            System.out.println("\nIs new message: " + folder.hasNewMessages());
            System.out.println("Count new message: " + folder.getNewMessageCount());
            System.out.println("Count message in folder:" + folder.getMessageCount() + "\n");/**/


            /*Message[] messages = folder.getMessages();
            //printMessages(messages);
            System.out.println("\n\nCount new message: " + messages.length);


            Message[] unreadMessages = folder.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false));          //взять все непрочитанные сообщения
            printMessages(unreadMessages);
            System.out.println("\n\nCount new message: " + unreadMessages.length);

            /*for (Message msg : unreadMessages)
            {
                mm.insertMessageInDB(msg);
            }

            /*mm.getConnect().createStatement().execute("Delete from mail;");
            for (Message msg : messages)
            {
                mm.insertMessageInDB(msg);
            } */


//            folder.setFlags(1, folder.getMessageCount(), new Flags (Flags.Flag.DELETED), true);
//            folder.close(true);
//            imap.close();
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
            //MailMessage.insertMessageInDB(messages[0]);
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

    private static void printMessages(Message[] messages) throws
                                                          IOException, MessagingException
    {

        for (Message msg : messages)
        {
            System.out.println(msg.getSubject());
            final Enumeration allHeaders = msg.getAllHeaders();
            while (allHeaders.hasMoreElements())
            {
                final Header header = (Header) allHeaders.nextElement();
                System.out.print("Name " + header.getName());
                System.out.println("\t Value " + header.getValue());
            }
            System.out.print(msg.getDisposition());
            System.out.println(msg.getDescription());
            //System.out.println((IMAPInputStream)msg.getContent());
            System.out.print("Body: ");
            final DataHandler handler = msg.getDataHandler();
            handler.writeTo(System.out);
            System.out.println("\n DataHandler: ");
            System.out.println(handler.getContent().toString());

            System.out.println(handler.getContentType());
            System.out.println(handler.getName());
            CommandInfo[] allCommands = handler.getAllCommands();
            for (CommandInfo command : allCommands)
            {
                System.out.println(command.getCommandClass());
                System.out.println(command.toString());
            }
            System.out.println("\n\n");
        }
    }
    
    private static void sendAllMail() throws Exception
    {
        MailMessage mm = new MailMessage();
        ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
        System.out.println(messagesToSend);
        for (Integer id : messagesToSend)
        {
            Message message = mm.createMessageOfDB(id);
            ConnectionDB.executeUpdate(
                    "UPDATE mail_status SET status=status & ~128 where mail_id = "
                    + id
            );
            ConnectionDB.executeUpdate(
                    "UPDATE mail SET status=status & ~128 where mail_id = "
                    + id);
            gNetSettings.getInstance().getSmtpTransport().sendMessage(message,
                                                                      message.getAllRecipients());
        }
    }

    public static void getAllUnreadMail() throws Exception
    {
        Store imap = gNetSettings.getInstance().getImapConnect();
        Folder folder = imap.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        Message[] unreadMessages = folder.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false));          //взять все непрочитанные сообщения
        printMessages(unreadMessages);

        System.out.println("\nIs new message: " + folder.hasNewMessages());
        System.out.println("Count message in folder:" + folder.getMessageCount() + "\n");
        System.out.println("\nCount new message: " + unreadMessages.length);

        MailMessage mm = new MailMessage();
        for (Message msg : unreadMessages)
        {
            mm.insertMessageInDB(msg);
        }

    }


}

