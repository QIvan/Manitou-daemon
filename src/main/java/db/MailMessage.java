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
                conn = new ConnectionDB("org.postgresql.Driver", "jdbc:postgresql:test");
                bd = conn.getConnect();
            } catch (Exception ex) {
                Logger.getLogger(MailMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

	/**
	 * @param msg looks like a message
	 * @throws Exception looks like SQLException
	 */
	public static void parseMsg(Message msg) throws Exception
        {
            //conn = new ConnectionDB("org.sqlite.JDBC", "jdbc:sqlite:MsgDB");
            conn = new ConnectionDB("org.postgresql.Driver", "jdbc:postgresql:test");
            bd = conn.getConnect();
            Statement st = bd.createStatement();

            // XXX Insert Message to different tables ,
            // здесь вызываем методы для добавление в разные таблицы сообщения msg
            // описываем их также в данном классе

            
            ResultSet rs = st.executeQuery("select * from mail"); 
            while (rs.next())
            {
                for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                    System.out.println(rs.getString(i));
                }
                System.out.println("\n\n");
            }
            // rs.close();

	}
        
        
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
                
                ;            
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
