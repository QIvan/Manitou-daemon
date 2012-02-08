package utils;

import db.ConnectionDB;
import net.gNetSettings;

import javax.mail.Flags;
import javax.mail.Folder;
import java.sql.Statement;

/**
 * Created 06.02.12 @ 16:13 by ivan.
 */
public class DeleteMails
{
    static public int[] DeleteAllInDB() throws Exception
    {
        final Statement delete = ConnectionDB.createStatement();
        delete.addBatch("Delete from mail_status");
        delete.addBatch("Delete from header");
        delete.addBatch("Delete from body");
        delete.addBatch("Delete from mail");
        return delete.executeBatch();
    }

    static public void DeleteAllInMailbox() throws Exception
    {
        final Folder inbox =
                gNetSettings.getInstance().getImapConnect().getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        inbox.setFlags(1, inbox.getMessageCount(),
                       new Flags(Flags.Flag.DELETED),
                       true);
        inbox.close(true);

    }


}
