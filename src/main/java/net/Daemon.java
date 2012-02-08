/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import db.ConnectionDB;
import db.MailMessage;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
            msg.writeTo(System.out);
            InputStream inputStream = msg.getDataHandler().getInputStream();

            int bytes_read = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];

            try {
            while((bytes_read = inputStream.read(data)) >0)
                baos.write(data, 0, bytes_read);
            inputStream.close();
            } catch(Exception e) {
                e.printStackTrace();
            }

            System.out.println(baos.toString());
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

