/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import db.ConnectionDB;
import db.MailMessage;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


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
            getAllUnreadMail();



        }
        catch (SQLException e) {
            System.err.print("\n======================\n");
            System.err.print(">>> Error in DB!");
            System.err.print("\n======================\n");
            e.printStackTrace();
        }
        catch (MessagingException e){
            System.err.print("\n======================\n");
            System.err.print(">>> Error in connect to server!");
            System.err.print("\n======================\n");
            e.printStackTrace();

        }
    }

    private static void printMessages(Message[] messages) throws
                                                           MessagingException
    {

        for (Message msg : messages)
        {
            try {
                msg.writeTo(System.out);
            }
            catch (IOException e)
            {
                System.err.println("Message is Empty! IOExeption in writeTo");
                e.printStackTrace();
            }

            System.out.println("\n\n");
        }
    }
    
    private static void sendAllMail() throws MessagingException,
                                             SQLException
    {
        MailMessage mm = new MailMessage();
        ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
        System.out.println(messagesToSend);
        for (Integer id : messagesToSend)
        {
            Message message = mm.createMessageOfDB(id);
            gNetSettings.getInstance().getSmtpTransport().sendMessage(message,
                                                                      message.getAllRecipients());
            ConnectionDB.executeUpdate(
                    "UPDATE mail_status SET status=status & ~128 where mail_id = "
                    + id
            );
            ConnectionDB.executeUpdate(
                    "UPDATE mail SET status=status & ~128 where mail_id = "
                    + id);
        }
    }

    public static void getAllUnreadMail() throws MessagingException,
                                                 SQLException
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

