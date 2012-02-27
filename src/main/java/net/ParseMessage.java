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
        if (msg.getContent() instanceof String)
            return (String)msg.getContent();

        DataContentHandler dcp = new text_plain();
        return (String) dcp.getContent(msg.getDataHandler().getDataSource());
    }

    public String getFrom()
    {
        String result = "";
        try{
            Address[] addresses = msg.getFrom();
            for (Address addr : addresses)
                result += (addr.toString() + " ");
        }
        catch (MessagingException e)
        {
            System.err.println("Warning! Error in getFrom class's Message");
            e.printStackTrace();
        }
        return result;
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

    public String getHeader()
    {
        String result = "";
        final String FROM = "From";
        result += FROM + ": " + addIfNotNull(FROM) + "\n";
        final String TO = "TO";
        result += TO + ": " + addIfNotNull(TO) + "\n";
        final String SUBJECT = "Subject";
        result += SUBJECT + ": " + addIfNotNull(SUBJECT) + "\n";
        final String DATE = "Date";
        result += DATE + ": " + addIfNotNull(DATE) + "\n";
        final String MESSAGE_ID = "Message-Id";
        result += MESSAGE_ID + ": " + addIfNotNull(MESSAGE_ID) + "\n";

        return result;
    }

    private String addIfNotNull (String type)
    {
        String result = "";
        try{
            String headers[] = msg.getHeader(type);
            if (headers.length != 0)
            {
                for (String hdr : headers)
                    result += hdr + " ";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Message getMsg()
    {
        return msg;
    }
}
