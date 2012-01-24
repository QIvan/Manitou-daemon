package db;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailMessage {

	private static ConnectionDB conn;
	private static Connection bd;
        
    public MailMessage()
    {
        try {
            conn = new ConnectionDB("org.postgresql.Driver", "jdbc:postgresql:test?user=ivan&password=qwertyui");
            bd = conn.getConnect();
        } catch (Exception ex) {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnect() throws Exception
    {
            return conn.getConnect();
    }

	/**
     * Записывает в БД сообщение msg
	 * @param msg looks like a message
	 * @throws Exception looks like SQLException
     * @return возвращает id сообщения из таблицы mail
	 */
	public int parseMsg(Message msg) throws Exception
    {

        Statement st = bd.createStatement();

        st.execute("INSERT INTO "
                   + "mail(sender, sender_fullname, subject, msg_date, sender_date, status, message_id)"
                   + " VALUES "
                   + "('manitou.test@gmail.com', 'manitou', 'subject',  now(), now(), 1, '398330e3-945b-4ae9-94f3')");

        ResultSet rs = st.executeQuery("Select mail_id from mail ");
        rs.next();
        return rs.getInt(1);

	}


    /**
     * @param id id сообщения в БД
     * @return возвращает сообщение в виде класса Message по переданному id
     */
    public Message createMessageOfDB (int id)
    {
        Message result = new MimeMessage(Session.getDefaultInstance(null));
        try {
            Statement infoSt = bd.createStatement();
            ResultSet infoRs = infoSt.executeQuery("Select sender, subject, msg_date from mail where mail_id=" + id);
            if (infoRs.next())
            {
                result.setFrom(new InternetAddress(infoRs.getString(1)));
                result.setSubject(infoRs.getString(2));
                result.setSentDate(infoRs.getDate(3));
            }
        } catch (Exception ex) {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
        

	public ArrayList<Integer> getMessagesToSend()
    {
        ArrayList<Integer> result = new ArrayList<Integer>();
        try {
            ResultSet mailIDs = bd.prepareStatement("Select mail_id from mail_status").executeQuery();
            while (mailIDs.next())
                result.add(mailIDs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
