package net;

import javax.mail.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created 24.01.12 @ 16:36 by i.zemlyansky.
 */
public class TransportMail
{
    public static boolean sendMail(Message message)
    {
        boolean result = true;
        try {
            Transport transport = gNetSettings.getInstance().getSmtpTransport();
            transport.sendMessage(message, message.getAllRecipients());
        }
        catch (Exception e)
        {
            Logger.getLogger(TransportMail.class.getName()).log(Level.SEVERE, null, e);
            result = false;
        }
        return result;
    }


    public static ArrayList<Message> getAllMails() throws MessagingException
    {
        ArrayList<Message> result = new ArrayList<Message>();
        getMailsProtocol(gNetSettings.getInstance().getPopConnect(), result);
        getMailsProtocol(gNetSettings.getInstance().getImapConnect(), result);
        return result;
    }
    
    private  static void getMailsProtocol(Store connect,
                                          ArrayList<Message> list) throws MessagingException
    {
        Folder inbox = connect.getFolder("INBOX");
        if (inbox == null) {
            System.out.println("No INBOX");
        }
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.getMessages();
        for (int i=0; i<messages.length; ++i)
            list.add(messages[i]);
    }

}
