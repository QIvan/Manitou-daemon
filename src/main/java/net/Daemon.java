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
import java.sql.Statement;
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
        catch (Exception e)
        {
            System.err.print("\n======================\n");
            System.err.print(">>> Unknown error!");
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
    

    private static void sendAllMail()
    {
        MailMessage mm = new MailMessage();
        ArrayList<Integer> messagesToSend = mm.getMessagesToSend();
        System.out.println(messagesToSend);
        for (Integer id : messagesToSend)
        {
            try {
                Message message = mm.createMessageOfDB(id);
                final Statement st = ConnectionDB.createStatement();
                try{
                    st.executeUpdate("BEGIN");
                    st.executeUpdate(
                            "UPDATE mail_status SET status=status & ~128 where mail_id = "
                            + id
                    );
                    st.executeUpdate(
                            "UPDATE mail SET status=status & ~128 where mail_id = "
                            + id);
                    System.out.println(message.getAllRecipients()[0].toString());
                    gNetSettings.getInstance().getSmtpTransport().sendMessage(
                            message,
                            message.getAllRecipients());
                    st.executeUpdate("COMMIT");
                }
                catch (MessagingException e)
                {
                    e.printStackTrace();
                    st.executeUpdate("ROLLBACK");
                }
                st.close();
            } catch (SQLException e){ e.printStackTrace();}
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

