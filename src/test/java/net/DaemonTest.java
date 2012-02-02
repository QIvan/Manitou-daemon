package net;

import junit.framework.TestCase;
import utils.NewMail;

/**
 * Created 02.02.12 @ 19:52 by i.zemlyansky.
 */
public class DaemonTest extends TestCase
{
    public void testGetMail() throws Exception
    {
        NewMail.sendTestMail();
        Daemon.getAllUnreadMail();

        //TODO проверить добавилось ли письмо в базу
    }

    public void testSendMail() throws Exception
    {

    }



    private void addTestMailInDB()
    {

    }

    // TODO сделать tearDown медод для удаления писем с ящика и из базы
}
