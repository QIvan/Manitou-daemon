/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;
import com.sun.mail.smtp.SMTPSSLTransport;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author ivan
 */
public class gNetSettings
{
    private static gNetSettings impl = null;
    private String popHost;
    private int    popPort;
    private String   imapHost;
    private int      imapPort;
    private String smtpHost;
    private int    smtpPort;
    /*private String username = "manitou.mail.test@gmail.com";
    private String password = "manitou1234";/**/
    private String username;// = "manitou.mail.test";
    private String password;// = "qwertyui";  /**/




    private Store popConnect = null;
    private Store imapConnect = null;

    public String getPassword()
    {
        return password;
    }


    private Transport smtpTransport = null;

    static public gNetSettings getInstance()
    {
        if (impl == null)
            impl = new gNetSettings();
        return impl;
    }

    private gNetSettings()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("properties." + this.getClass().getName());
        popHost = bundle.getString("popHost");
        popPort = Integer.parseInt(bundle.getString("popPort"));
        imapHost = bundle.getString("imapHost");
        imapPort = Integer.parseInt(bundle.getString("imapPort"));
        smtpHost = bundle.getString("smtpHost");
        smtpPort = Integer.parseInt(bundle.getString("smtpPort"));
        username = bundle.getString("username");
        password = bundle.getString("password");

    }

    /*public String getHost()
    {
        return popHost;
    }

    public int getPort()
    {
        return popPort;
    }*/

    public String getUsername()
    {
        return username;
    }


    public Store getPopConnect() throws MessagingException
    {
        if (popConnect == null)
        {
            try
            {
                popConnect = new POP3SSLStore(Session.getDefaultInstance(new Properties()),
                                         new URLName(popHost));
                popConnect.connect(popHost, popPort, username, password);
            }
            catch (MessagingException e)
            {
                popConnect = new POP3Store(Session.getDefaultInstance(new Properties()),
                                      new URLName(popHost));
                popConnect.connect(popHost, popPort, username, password);
            }
        }
        return popConnect;
    }


    public Store getImapConnect() throws MessagingException
    {
        if (imapConnect == null)
        {

            try
            {
                imapConnect = new IMAPSSLStore(Session.getDefaultInstance(new Properties()),
                                         new URLName(imapHost));
                imapConnect.connect(imapHost, imapPort, username, password);
            }
            catch (MessagingException e)
            {
                imapConnect = new IMAPStore(Session.getDefaultInstance(new Properties()),
                                      new URLName(imapHost));
                imapConnect.connect(imapHost, imapPort, username, password);
            }
        }
        return imapConnect;
    }

    public Transport getSmtpTransport() throws MessagingException
    {
        if (smtpTransport == null)
        {
            try
            {
                smtpTransport = new SMTPSSLTransport(Session.getDefaultInstance(
                        new Properties()),
                                                     new URLName(smtpHost));
                smtpTransport.connect(smtpHost, smtpPort, username, password);
            }
            catch (MessagingException e)
            {
                smtpTransport =
                        new SMTPTransport(Session.getDefaultInstance(new Properties()),
                                          new URLName(smtpHost));
                smtpTransport.connect(smtpHost, smtpPort, username, password);
            }
        }
        return smtpTransport;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (smtpTransport.isConnected()) smtpTransport.close();
        if (imapConnect.isConnected()) imapConnect.close();
        if (popConnect.isConnected()) popConnect.close();
    }
}
