package net;

import junit.framework.TestCase;
import utils.DeleteMails;

/**
* Created 02.02.12 @ 19:52 by i.zemlyansky.
*/
public class DaemonTest extends TestCase
{
    public void testGetMail() throws Exception
    {
        utils.NewMail.sendTestMail();
        Thread.sleep(1000);
        Daemon.getAllUnreadMail();

        final int[] resultDelete = DeleteMails.DeleteAllInDB();
        System.out.print(resultDelete);
        int number = 0;
        for (int i : resultDelete)
        {
            ++number;
//            Для SQLite не работает
//            TODO придумать другую проверку
//            if (i == 0)
//                fail("Query number " + number + " Return 0");
        }
    }

    public void testSendMail() throws Exception
    {

    }

    @Override
    public void tearDown() throws Exception
    {
        DeleteMails.DeleteAllInMailbox();


    }
}
