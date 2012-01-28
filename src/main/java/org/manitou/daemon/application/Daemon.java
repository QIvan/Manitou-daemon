package org.manitou.daemon.application;

/**
 * this is the main starter class for daemon
 *
 * so how will it work
 * 1. dbms -- we can have embeddable OR service instance
 *      1-a. sqlite: each user should have an instance of DB file --
 *                          so it'll be located @ ${user.home}
 *      1-b. postgresql: I. parameter string with connection options
 *                          should be given with cli -- or
 *                       II. configuration file @ ${user.home} --
 *                          which seems like more secure to me
 * 2. mail server OR list of servers
 *          cli arguments
 *      OR
 *          configuration file
 * 3. there should be a limitation of one instance of daemon per user --
 *      so should be some lock file @ ${user.home}
 * 4. Signals
 *      4-a: send mail
 *      4-b: check remote mail
 *      4-c: reread configuration
 *      4-d: we've got new items @ db
 */
public class Daemon
{
    private static Daemon ourInstance = new Daemon();

    public static Daemon getInstance()
    {
        return ourInstance;
    }

    private Daemon()
    {
    }

    public static void main(String[] args)
    {
        // looks like we will just delegating logic for now
        net.Daemon.main(args);

        // todo parse args -- use jcommander
        // todo init application
        // todo process lifecycle
    }
}
