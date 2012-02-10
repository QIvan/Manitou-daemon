package net;

import com.sun.mail.handlers.text_plain;

import javax.activation.DataContentHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created 10.02.12 @ 18:55 by i.zemlyansky.
 */
public class ParseMessage
{
    private Message msg;

    public ParseMessage(Message message)
    {
        this.msg = message;
    }

    public String getBody() throws IOException, MessagingException
    {
        DataContentHandler dcp = new text_plain();
        return (String) dcp.getContent(msg.getDataHandler().getDataSource());
    }

    public Address[] getFrom() throws MessagingException
    {
        return msg.getFrom();
    }

    public Date getSentDate() throws MessagingException
    {
        return msg.getSentDate();
    }

    @Override
    public String toString()
    {
        try
        {
            OutputStream os = new ByteArrayOutputStream();
            msg.writeTo(os);
            os.flush();
            return os.toString();
        }
        catch (Exception e)
        {
            return "Parse Error";
        }
    }

    public String getSubject()
    {
        try {
            return msg.getSubject();
        } catch (Exception e)
        {
            return "";
        }
    }


    public String getMessageID ()
    {
        try
        {
            return msg.getHeader("Message-ID")[0];
        }
        catch (MessagingException e)
        {
            return "";
        }
    }
}
